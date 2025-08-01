package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.SchoolYearDTO;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.enums.EnrollmentType;
import com.sillador.strecs.enums.StudentStatus;
import com.sillador.strecs.repositories.*;
import com.sillador.strecs.services.*;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.CodeGenerator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SchoolYearServiceImpl extends BaseService implements SchoolYearService {

    private static Logger logger = LoggerFactory.getLogger(SchoolYearServiceImpl.class);

    private final SchoolYearRepository schoolYearRepository;
    private final EnrollmentService enrollmentService;
    private final YearLevelRepository yearLevelRepository;
    private final StudentService studentService;
    private final SubjectCodeRepository subjectCodeRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    public SchoolYearServiceImpl(SchoolYearRepository schoolYearRepository,
                                 EnrollmentService enrollmentService,
                                 YearLevelRepository yearLevelRepository,
                                 StudentService studentService,
                                 SubjectCodeRepository subjectCodeRepository,
                                 SectionRepository sectionRepository,
                                 SubjectRepository subjectRepository
                                 ) {
        this.schoolYearRepository = schoolYearRepository;
        this.enrollmentService = enrollmentService;
        this.yearLevelRepository = yearLevelRepository;
        this.studentService = studentService;
        this.subjectCodeRepository = subjectCodeRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public BaseResponse getAll(Map<String, String> query) {
        return null;
    }

    @Override
    public Optional<SchoolYear> findById(Long schoolYear) {
        return Optional.empty();
    }

    @Override
    public BaseResponse openNewSchoolYear(SchoolYearDTO schoolYearDTO) {
        Optional<SchoolYear> schoolYearOptional = schoolYearRepository.findByYear(schoolYearDTO.getYear());
        if(schoolYearOptional.isPresent()){
            return error("School year " + schoolYearDTO.getYear() + " already exist");
        }

        SchoolYear schoolYear = new SchoolYear();
        schoolYear.setOpening(schoolYearDTO.getOpening());
        schoolYear.setClosing(schoolYearDTO.getClosing());
        schoolYear.setYear(schoolYearDTO.getYear());
        schoolYear.setCurrent(true);

        // get the current active school year and close it
        SchoolYear currentSchoolYear = schoolYearRepository.findByIsCurrent(true).orElse(null);
        if(currentSchoolYear != null) {
            currentSchoolYear.setCurrent(false);
            schoolYearRepository.save(currentSchoolYear);
        }

        // Save the new active current school year
        schoolYear = schoolYearRepository.save(schoolYear);

        return prepareAllStudents(currentSchoolYear, schoolYear);
    }

    public BaseResponse prepareAllStudents(@NotNull SchoolYear oldSchoolYear, @NotNull  SchoolYear newSchoolYear){
        List<Enrollment> enrollmentList = enrollmentService.findAllEnrolledStudents(oldSchoolYear);
        YearLevel yearLevel = yearLevelRepository.findLastYearLevelByLevelOrder().orElse(null);
        if(yearLevel == null){
            return error("Year level is not properly set");
        }

        // 1. Generate sections based from previous school year
        List<Section> sections = sectionRepository.findAll();
        for(Section section : sections){
            logger.info("Section detected : {} {} {}", section.getCode(), section.getSchoolYear(), section.getYearLevel().getName());
            Section copy = new Section();
            BeanUtils.copyProperties(section, copy, "id", "createdDate");

            // Check section by code and the next school year if already exist from the DB
            Optional<Section> sectionOptional = sectionRepository.findByCodeAndYearLevelAndSchoolYear(copy.getCode(), copy.getYearLevel(), newSchoolYear.getYear());

            // Save the new section if not exist
            if(sectionOptional.isEmpty()){

                logger.info("Section created : {} {} {}", copy.getCode(), copy.getSchoolYear(), copy.getYearLevel().getName());

                copy.setSchoolYear(newSchoolYear.getYear());
                copy = sectionRepository.save(copy);

            }else{
                copy = sectionOptional.get();

                logger.info("Section exist : {} {} {}", copy.getCode(), copy.getSchoolYear(), copy.getYearLevel().getName());
            }

            // 2. Generate subject codes based from previous school year
            List<Subject> subjects = subjectRepository.findAllByYearLevelAndActive(copy.getYearLevel(), true);
            for(Subject subject : subjects){
                SubjectCode subjectCode = new SubjectCode();
                subjectCode.setSubject(subject);
                subjectCode.setSection(copy);
                subjectCode.setAdviser(copy.getAdviser());
                subjectCode.setSchoolYear(copy.getSchoolYear());

                subjectCode.setCode(CodeGenerator.generateCode());

                // Check if the same subjects already exist from the DB
                Optional<SubjectCode> subjectCodeOptional = subjectCodeRepository.findByCodeAndSectionAndSubjectAndSchoolYear(subjectCode.getCode(), subjectCode.getSection(), subjectCode.getSubject(), subjectCode.getSchoolYear());
                if(subjectCodeOptional.isEmpty()) {
                    subjectCodeRepository.save(subjectCode);
                    logger.info("Subject code created : {} {}", subjectCode.getCode(), subjectCode.getSubject().getCode());
                }else{
                    logger.info("Subject code existed : {} {}", subjectCode.getCode(), subjectCode.getSubject().getCode());
                }
            }
        }






        enrollmentList.forEach(d->{
            YearLevel studentYearLevel = d.getYearLevel();
            Student student = d.getStudent();
            // Processed only the students who is actively enrolled
            if(student.getStatus() == StudentStatus.ENROLLED) {
                if (studentYearLevel.getId() == yearLevel.getId()) {
                    // 1. Do changes to make the student graduate
                    // 2. Check for grades if incomplete.
                    student.setStatus(StudentStatus.GRADUATED);

                } else {
                    // 1. Change if the student is incomplete
                    // 2. Enroll student to new school year

                    student.setStatus(StudentStatus.OPEN);

                    Enrollment enrollment = new Enrollment();
                    // Allow student to be enroll on the next school year
                    enrollment.setSchoolYear(newSchoolYear.getYear());
                    enrollment.setStudent(student);
                    enrollment.setEnrollmentType(EnrollmentType.EXISTING);
                    // Add 1 year level

                    int yearLevelOrder = d.getYearLevel().getLevelOrder();
                    // 3. Get the next year level to be assigned automatically
                    Optional<YearLevel> newYearLevel = yearLevelRepository.findByLevelOrder(yearLevelOrder + 1);
                    newYearLevel.ifPresent(enrollment::setYearLevel);

                    System.out.println(student.getStudentId() + " Enrolled to grade " + enrollment.getYearLevel().getName() +" from " + d.getYearLevel().getName() );

                    // 4. Save the student enrollment candidate
                    enrollmentService.save(enrollment);


                }

                studentService.save(student);
            }

        });

        return  success();
    }

}
