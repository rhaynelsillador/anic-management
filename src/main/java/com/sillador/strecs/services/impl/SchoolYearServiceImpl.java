package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.InitSchoolYearDTO;
import com.sillador.strecs.dto.SchoolYearDTO;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.enums.EnrollmentType;
import com.sillador.strecs.enums.StudentStatus;
import com.sillador.strecs.repositories.*;
import com.sillador.strecs.services.*;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.CodeGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolYearServiceImpl extends BaseService implements SchoolYearService {

    private static Logger logger = LoggerFactory.getLogger(SchoolYearServiceImpl.class);

    private final SchoolYearRepository schoolYearRepository;
    private final EnrollmentService enrollmentService;
    private final YearLevelRepository yearLevelRepository;
    private final StudentService studentService;
    private final SubjectCodeRepository subjectCodeRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public BaseResponse getAll(Map<String, String> query) {
        List<SchoolYear> schoolYears = schoolYearRepository.findAll();
        BaseResponse baseResponse = new BaseResponse().build(schoolYears);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(schoolYears.size(), schoolYears.size()));
        return baseResponse;
    }

    @Override
    public Optional<SchoolYear> findById(Long schoolYear) {
        return schoolYearRepository.findById(schoolYear);
    }



    @Override
    public BaseResponse createNewSchoolYear(SchoolYearDTO schoolYearDTO) {
        SchoolYear schoolYear = new SchoolYear();
        schoolYear.setCurrent(false);
        schoolYear.setOpening(schoolYearDTO.getOpening());
        schoolYear.setClosing(schoolYearDTO.getClosing());
        schoolYear.setYear(schoolYearDTO.getYear());

        schoolYearRepository.save(schoolYear);
        return success();
    }

    @Override
    public BaseResponse updateSchoolYear(long id, SchoolYearDTO schoolYearDTO) {
        Optional<SchoolYear> schoolYearOptional = schoolYearRepository.findById(id);
        if(schoolYearOptional.isEmpty()){
            return error("School year not found");
        }
        SchoolYear schoolYear = schoolYearOptional.get();
        schoolYear.setOpening(schoolYearDTO.getOpening());
        schoolYear.setClosing(schoolYearDTO.getClosing());
        schoolYear.setYear(schoolYearDTO.getYear());

        schoolYearRepository.save(schoolYear);
        return success();
    }

    @Override
    public BaseResponse openNewSchoolYear(InitSchoolYearDTO initSchoolYearDTO) {
        Optional<SchoolYear> schoolYearOptional = schoolYearRepository.findByYear(initSchoolYearDTO.getSchoolYearId());
        if(schoolYearOptional.isEmpty()){
            return error("School year " + initSchoolYearDTO.getSchoolYearId() + " does not exist");
        }

        SchoolYear schoolYear = schoolYearOptional.get();
        // Check if the school year is already active
        schoolYear.setCurrent(true);

        // get the current active school year and close it
        SchoolYear currentSchoolYear = schoolYearRepository.findByIsCurrent(true).orElse(null);
        if(currentSchoolYear != null) {

            // Check if the new school year is greater than the current school year
            if(currentSchoolYear.getYear() > schoolYear.getYear()){
                return error("School year " + schoolYear.getYear() + " must be greater than the current school year " + currentSchoolYear.getYear());
            }else if(currentSchoolYear.getYear() == schoolYear.getYear()){
                return error("You cannot reinitialize the current school year " + currentSchoolYear.getYear() + ". Please select a different school year.");
            }

            currentSchoolYear.setCurrent(false);
            schoolYearRepository.save(currentSchoolYear);
        }

        // Save the new active current school year
        schoolYear = schoolYearRepository.save(schoolYear);

        System.out.println("current school year opened : " + currentSchoolYear.getYear());
        System.out.println("New School Year ID : " + schoolYear.getId());

        if(currentSchoolYear == null){
            return success();
        }
        return prepareAllStudents(currentSchoolYear, schoolYear, initSchoolYearDTO);
    }

    public BaseResponse prepareAllStudents(@NotNull SchoolYear oldSchoolYear, @NotNull  SchoolYear newSchoolYear, InitSchoolYearDTO initSchoolYearDTO){

        // 1. Generate sections based from previous school year
        List<Section> sections = sectionRepository.findAllBySchoolYear(oldSchoolYear.getYear());
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

        List<Enrollment> enrollmentList = enrollmentService.findAllEnrolledStudents(oldSchoolYear);

        enrollmentList.forEach(d->{
            Student student = d.getStudent();
            System.out.println("Processing student: " + student.getFullName() + " with status: " + d.getStatus());
            // Processed only the students who are actively enrolled
            if(d.getStatus() == StudentStatus.OPEN || d.getStatus() == StudentStatus.ENROLLED) {
                Optional<YearLevel> newYearLevel = yearLevelRepository.findByPrerequisiteYear(d.getYearLevel());

                System.out.println("Student : " + student.getStudentId() + " with year level: " + d.getYearLevel().getName() + " new year level: " + newYearLevel.map(YearLevel::getName).orElse("None"));

                if (newYearLevel.isEmpty()) {

                    d.setGraduated(initSchoolYearDTO.getGraduationDate());
                    d.setStatus(StudentStatus.GRADUATED);


                    d.setBatch(initSchoolYearDTO.getBatch());

                    enrollmentService.save(d);

                } else {
                    Enrollment enrollment = new Enrollment();
                    // Allow student to be enroll on the next school year
                    enrollment.setSchoolYear(newSchoolYear.getYear());
                    enrollment.setStudent(student);
                    enrollment.setEnrollmentType(EnrollmentType.EXISTING);
                    enrollment.setStatus(StudentStatus.OPEN);

                    System.out.println("Enrolling student: " + student.getFullName() + " to the next year level " + d.getYearLevel().getName());
                    /// Get the next year level based on the prerequisite year level

                    newYearLevel.ifPresent(enrollment::setYearLevel);

                    System.out.println(enrollment.getYearLevel() + " >>> ");
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
