package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.*;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.repositories.GradeRepository;
import com.sillador.strecs.repositories.GradingPeriodRepository;
import com.sillador.strecs.repositories.SchoolYearRepository;
import com.sillador.strecs.repositories.SubjectCodeRepository;
import com.sillador.strecs.repositories.specifications.SubjectCodeSpecification;
import com.sillador.strecs.services.*;
import com.sillador.strecs.utility.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectCodeServiceImpl extends BaseService implements SubjectCodeService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SubjectCodeServiceImpl.class);

    private final SubjectCodeRepository subjectCodeRepository;
    private final SubjectService subjectService;
    private final RoomService roomService;
    private final SectionService sectionService;
    private final TeacherService teacherService;
    private final EnrollmentService enrollmentService;
    private final GradeRepository gradeRepository;
    private final StudentService studentService;
    private final SchoolYearRepository schoolYearRepository;
    private final GradingPeriodRepository gradingPeriodRepository;


    @Override
    public BaseResponse getAll(@NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));


        Sort sort = SubjectCodeSpecification.sorting(sorting, SubjectCodeSpecification.getSortedKeys());
        Specification<SubjectCode> spec = SubjectCodeSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<SubjectCode> pages = subjectCodeRepository.findAll(spec, pageable);

        List<SubjectCodeDTO> dos = new ArrayList<>();
        pages.getContent().forEach(d -> dos.add(toDTO(d)));

        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.getTotalElements(), pages.getSize()));
        return baseResponse;
    }

    @Override
    public SubjectCode create(SubjectCode subjectCode) {
        return subjectCodeRepository.save(subjectCode);
    }

    @Override
    public BaseResponse save(SubjectCodeRequestDTO subjectCodeRequest) {
        Optional<Section> sectionOptional = sectionService.findById(subjectCodeRequest.getSection());
        if(sectionOptional.isEmpty()){
            return error("Section is required");
        }

        Optional<SchoolYear> schoolYear = schoolYearRepository.findByIsCurrent(true);
        if(schoolYear.isEmpty()){
            return error("School year not yet started");
        }
//        List<SubjectCode> subjectCodes = new ArrayList<>();
        for (SubjectCodeDTO dto : subjectCodeRequest.getSubjects()){
            // Check DB if the subjectCode.code already exist

//            SubjectCode subjectCode = subjectCodeRepository.findByCode(dto.getCode()).orElse(new SubjectCode());


            // Check subjectCode if exist from the DB and return an error if does not exist
            Optional<Subject> subject = subjectService.findByCode(dto.getSubjectCode());
            if(subject.isEmpty()){
                return error("Subjects is required");
            }


            // Get the current subject code to enable the editing
            SubjectCode subjectCode = subjectCodeRepository.findByCode(dto.getCode()).orElse(null);

            // If the code does not exist or null check the subject code if already tagged
            if(subjectCode == null) {
                Optional<SubjectCode> subjectCodeTmp = subjectCodeRepository.findBySectionAndSubjectAndSchoolYear(sectionOptional.get(), subject.get(), schoolYear.get().getYear());
                if (subjectCodeTmp.isPresent()) {
                    subjectCode = subjectCodeTmp.get();
                } else {
                    subjectCode = new SubjectCode();
                    subjectCode.setEndTime(dto.getEndTime());
                    subjectCode.setStartTime(dto.getStartTime());
                }
            }


            System.out.println(subjectCode.getId() + "<><>" + subjectCode.getCode());

            subjectCode.setSubject(subject.get());
            // Set value if adding new subject code
            if(dto.getId() == 0) {
                subjectCode.setSchoolYear(schoolYear.get().getYear());
                subjectCode.setCode(CodeGenerator.generateCode());
            }
            // Set weekdays as default class days
            if(subjectCode.getDays() == null || subjectCode.getDays().isEmpty()){
                subjectCode.setDays(Days.WEEKDAYS);
            }



            if(dto.getRoom() != null && !dto.getRoom().isBlank() && StringValidator.isNumeric(dto.getRoom())){
                Optional<Room> roomOption = roomService.findById(Long.parseLong(dto.getRoom()));
                subjectCode.setRoom(roomOption.orElse(null));
            }
            System.out.println(dto.getAdviser());
            if(dto.getAdviser() != null && !dto.getAdviser().isBlank() && StringValidator.isNumeric(dto.getAdviser())){
                Optional<Teacher> teacherOptional = teacherService.findById(Long.parseLong(dto.getAdviser()));
                System.out.println(teacherOptional.get().getFirstName());
                subjectCode.setAdviser(teacherOptional.orElse(null));
            }else{
                subjectCode.setAdviser(null);
            }

            subjectCode.setSection(sectionOptional.get());

            subjectCodeRepository.save(subjectCode);
//            subjectCodes.add(subjectCode);
        }
//        subjectCodeRepository.saveAll(subjectCodes);
        return new BaseResponse().success();
    }

    @Override
    public BaseResponse getSubjectsByAdviser(Long adviserId, long schoolYear) {
        Optional<Teacher> teacher = teacherService.findById(adviserId);
        if(teacher.isEmpty()){
            return error("Invalid adviser request");
        }
        List<SubjectCode> subjectCodes = subjectCodeRepository.findAllByAdviserAndSchoolYear(teacher.get(), schoolYear);
        List<SubjectCodeDTO> dos = new ArrayList<>();
        subjectCodes.forEach(d -> dos.add(toDTO(d)));
        BaseResponse baseResponse = success("Success").build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(subjectCodes.size(), 0));
        return baseResponse;
    }

    @Override
    public BaseResponse getStudentsBySubjectCode(String code){
        Optional<SubjectCode> subjectCode = subjectCodeRepository.findByCode(code);
        if(subjectCode.isEmpty()){
            return error("Invalid subject code request.");
        }

        List<Enrollment> enrollments = enrollmentService.findAllBySection(subjectCode.get().getSection());
        List<EnrollmentDTO> dos = new ArrayList<>();
        enrollments.forEach(d-> dos.add(enrollmentService.toDTO(d, true)));
        BaseResponse baseResponse = success("Success").build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(enrollments.size(), 0));
        return baseResponse;
    }

    @Override
    public Optional<SubjectCode> findByCode(String subjectCode) {
        return subjectCodeRepository.findByCode(subjectCode);
    }

    @Override
    public List<AcademicRecordSubjectCodeDTO> findAllByEnrollmentAsDTO(Enrollment enrollment) {

        List<AcademicRecordSubjectCodeDTO> dtos = new ArrayList<>();
        subjectCodeRepository.findAllBySection(enrollment.getSection()).forEach(d -> {
            SubjectCode subjectCode = d;
            Subject subject = subjectCode.getSubject();

            AcademicRecordSubjectCodeDTO subjectInfo = new AcademicRecordSubjectCodeDTO();
            subjectInfo.setAdviser(subjectInfo.getAdviser());
            subjectInfo.setSubjectName(subject.getName());
            subjectInfo.setSubjectCode(subjectCode.getCode());
            if(subjectCode.getRoom() != null) {
                subjectInfo.setRoomNum(subjectCode.getRoom().getRoomNo() + " " + subjectCode.getRoom().getBuilding());
            }
            subjectInfo.setFirstQuarter(0);
            subjectInfo.setSecondQuarter(0);
            subjectInfo.setThirdQuarter(0);
            subjectInfo.setFourthQuarter(0);
            subjectInfo.setFinalGrade(0);
            subjectInfo.setUnits(subject.getUnits());
            subjectInfo.setStatus("Not yet graded");
            if(subjectCode.getAdviser() != null) {
                subjectInfo.setAdviser(subjectCode.getAdviser().getFullName());
            }

            subjectInfo.setGroupYearLevel(subject.getYearLevel().getGroupYearLevel());

            gradeRepository.findAllBySubjectCode(subjectCode).forEach(grade -> {
                switch (grade.getGradingPeriod()) {
                    case 1 -> subjectInfo.setFirstQuarter(grade.getGradeScore());
                    case 2 -> subjectInfo.setSecondQuarter(grade.getGradeScore());
                    case 3 -> subjectInfo.setThirdQuarter(grade.getGradeScore());
                    case 4 -> subjectInfo.setFourthQuarter(grade.getGradeScore());
                    case 5 -> subjectInfo.setFinalGrade(grade.getGradeScore());
                    default -> logger.warn("Unknown grading period: {}", grade.getGradingPeriod());
                }
                
            });

            if(subjectInfo.getFirstQuarter() > 0 || subjectInfo.getSecondQuarter() > 0 || subjectInfo.getThirdQuarter() > 0 || subjectInfo.getFourthQuarter() > 0) {
                subjectInfo.setStatus("Graded");
                subjectInfo.setRemarks("Passed");
            } else {
                subjectInfo.setStatus("Not yet graded");
                subjectInfo.setRemarks("Not yet graded");
            }

            dtos.add(subjectInfo);
        });
        
        return dtos;
    }


    @Override
    public BaseResponse getStudentRecords(long id) {
        Optional<Student> studentOptional = studentService.findById(id);
        if(studentOptional.isEmpty()){
            return error("Student does not exist");
        }
        List<Enrollment> enrollments = enrollmentService.findAllByStudentOrderBySchoolYearAsc(studentOptional.get());

        List<AcademicRecordDTO> academicRecordDTOS = new ArrayList<>();
        // Generate enrollment records without student info

        enrollments.forEach(d -> {
            AcademicRecordDTO recordDTO = new AcademicRecordDTO();
            recordDTO.setEnrollment(enrollmentService.toDTO(d, false));

            recordDTO.setSubjects(findAllByEnrollmentAsDTO(d));

            academicRecordDTOS.add(recordDTO);
        });
        return success().build(academicRecordDTOS);
    }


    private SubjectCodeDTO toDTO(SubjectCode d){
        SubjectCodeDTO dto = new SubjectCodeDTO();
        dto.setId(d.getId());
        dto.setCode(d.getCode());
        if(d.getAdviser() != null) {
            dto.setAdviser(d.getAdviser().getFullName());
        }
        dto.setSubjectCode(d.getSubject().getCode());
        dto.setSubjectName(d.getSubject().getName());
        dto.setSchoolYear(d.getSchoolYear());
        dto.setStartTime(d.getStartTime());
        dto.setEndTime(d.getEndTime());

        dto.setActive(d.isActive());
        dto.setLocked(d.isLocked());

        Subject subject = d.getSubject();
        dto.setYearLevel(subject.getYearLevel().getName());
        dto.setUnits(subject.getUnits());

        dto.setSection(d.getSection().getCode());
        if(d.getRoom() != null) {
            dto.setRoom(d.getRoom().getBuilding() + " - " + d.getRoom().getRoomNo());
        }

        dto.setCreatedDate(d.getCreatedDate());
        dto.setUpdatedDate(d.getUpdatedDate());


        return dto;
    }
}
