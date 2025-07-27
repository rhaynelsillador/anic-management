package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.*;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.enums.EnrollmentType;
import com.sillador.strecs.enums.StudentStatus;
import com.sillador.strecs.repositories.EnrollmentRepository;
import com.sillador.strecs.repositories.SchoolYearRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.EnrollmentSpecification;
import com.sillador.strecs.services.EnrollmentService;
import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.services.StudentService;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.Config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl extends BaseService implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final SectionService sectionService;
    private final YearLevelService yearLevelService;
    private final SchoolYearRepository schoolYearRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentService studentService, SectionService sectionService, YearLevelService yearLevelService, SchoolYearRepository schoolYearRepository){
        this.enrollmentRepository = enrollmentRepository;
        this.studentService = studentService;
        this.sectionService = sectionService;
        this.yearLevelService = yearLevelService;
        this.schoolYearRepository = schoolYearRepository;
    }


    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));

        Sort sort = BaseSpecification.sorting(sorting, EnrollmentSpecification.getSortedKeys());
        Specification<Enrollment> spec = EnrollmentSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Enrollment> pages = enrollmentRepository.findAll(spec, pageable);

        List<EnrollmentDTO> dos = new ArrayList<>();
        pages.getContent().forEach(d -> dos.add(toDTO(d)));

        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.getTotalElements(), pages.getSize()));
        return baseResponse;
    }

    @Override
    public BaseResponse enrollStudents(List<StudentLevelAssignmetDTO> dtos) {
        Optional<SchoolYear> schoolYear = schoolYearRepository.findByIsCurrent(true);
        if(schoolYear.isEmpty()){
            return success().build(new ArrayList<>());
        }
        List<Enrollment> enrollments = new ArrayList<>();
        for (StudentLevelAssignmetDTO dto : dtos){
            Optional<Student> student = studentService.findById(dto.getStudent());
            Optional<Section> section = sectionService.findById(dto.getSection());
            Optional<YearLevel> yearLevel = yearLevelService.findById(dto.getYearLevel());

            if (student.isEmpty()) {
                return error("Invalid student request");
            }else if(section.isEmpty()){
                return error("Invalid section request");
            }else if(yearLevel.isEmpty()){
                return error("Invalid year level request");
            }
            Student student1 = student.get();
            if(student1.getStatus() == StudentStatus.ENROLLED){
                return error("Invalid student request with LRN : " + student1.getLrn() + " detected has already enrolled.");
            }



            // Check if the student already enrolled for current school year
            Enrollment enrollment = enrollmentRepository.findByStudentAndSchoolYear(student1, schoolYear.get().getYear()).orElse(new Enrollment(student1, section.get()));
            enrollment.setYearLevel(yearLevel.get());
            enrollment.setSection(section.get());
            enrollment.setEnrollmentDate(new Date(System.currentTimeMillis()));
            enrollment.setSchoolYear(schoolYear.get().getYear());
            enrollment.setEnrollmentType(EnrollmentType.EXISTING);

            enrollments.add(enrollment);
        }
        // Tag student as ENROLLED to remove from the Not Enrolled list
        for (Enrollment enrollment : enrollments){
            Student student = enrollment.getStudent();
            student.setStatus(StudentStatus.ENROLLED);
            // Update Student status as Enrolled
            studentService.save(student);
        }

        // Save as array to have one transaction only on the Database
        enrollmentRepository.saveAll(enrollments);

        return success("Students successfully enrolled.");
    }

    @Override
    public BaseResponse enrollStudent(NewStudentDTO studentDTO) {
        Student student = studentService.newStudent(studentDTO);
        Optional<SchoolYear> schoolYear = schoolYearRepository.findByIsCurrent(true);
        if(schoolYear.isEmpty()){
            return error("School year is not open.");
        }

//        Config config = new Config();
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setEnrollmentType(studentDTO.getEnrollmentType());
        enrollment.setEnrollmentDate(new Date(System.currentTimeMillis()));
        enrollment.setSchoolYear(schoolYear.get().getYear());

        if(studentDTO.getSection() != null){
            Optional<Section> section = sectionService.findById(studentDTO.getSection());
            if(section.isEmpty()){
                return error("Invalid Section is selected");
            }
            enrollment.setSection(section.get());
        }

        Optional<YearLevel> yearLevel = yearLevelService.findById(studentDTO.getYearLevel());
        if(yearLevel.isEmpty()){
            return error("Invalid Grade level selected");
        }

        student.setStatus(StudentStatus.ENROLLED);
        studentService.update(student);

        enrollment.setYearLevel(yearLevel.get());

        enrollment = enrollmentRepository.save(enrollment);
        return success("Successfully enrolled").build(enrollment);
    }

    @Override
    public List<Enrollment> findAllBySectionAndSubject(Section section) {
        return enrollmentRepository.findAllBySection(section);
    }

    @Override
    public EnrollmentDTO toDTO(Enrollment d){
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(d.getId());
        dto.setEnrollmentDate(d.getEnrollmentDate());
        if(d.getSection() != null) {
            dto.setSection(d.getSection().getCode());
            dto.setAdviser(d.getSection().getAdviser().getFullName());
        }
        dto.setSchoolYear(d.getSchoolYear());

        Student student = d.getStudent();
        dto.setStudent(toStudentDTO(student));
        if(d.getYearLevel() != null) {
            dto.setYearLevel(d.getYearLevel().getName());
        }

        dto.setEnrollmentType(d.getEnrollmentType().name());


        return dto;
    }

    @Override
    public Optional<Enrollment> findByStudentAndSchoolYear(Student student, int schoolYear) {
        return enrollmentRepository.findByStudentAndSchoolYear(student, schoolYear);
    }

    @Override
    public List<Enrollment> findAllEnrolledStudents(SchoolYear oldSchoolYear) {
        return enrollmentRepository.findAllBySchoolYear(oldSchoolYear.getYear());
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    private StudentDTO toStudentDTO(Student student){
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setStudentId(student.getStudentId());
        studentDTO.setLrn(student.getLrn());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());
        studentDTO.setMiddleName(student.getMiddleName());
        studentDTO.setBirthday(student.getBirthday());
        studentDTO.setContactNumber(student.getContactNumber());
        studentDTO.setGender(student.getGender());
        studentDTO.setStatus(student.getStatus());

        studentDTO.setFullName((student.getLastName() + ", " + student.getFirstName() + " " + student.getMiddleName())
                .replace("null", "")
                .trim());

        return studentDTO;
    }
}
