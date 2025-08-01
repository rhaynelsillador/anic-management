package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.*;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.repositories.GradeRepository;
import com.sillador.strecs.repositories.GradingPeriodRepository;
import com.sillador.strecs.services.*;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GradeServiceImpl extends BaseService implements GradeService {

    private final GradeRepository gradeRepository;
    private final SubjectCodeService subjectCodeService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final GradingPeriodRepository gradingPeriodRepository;


    public GradeServiceImpl(GradeRepository gradeRepository, SubjectCodeService subjectCodeService, EnrollmentService enrollmentService, StudentService studentService, GradingPeriodRepository gradingPeriodRepository){
        this.gradeRepository = gradeRepository;
        this.subjectCodeService = subjectCodeService;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.gradingPeriodRepository = gradingPeriodRepository;
    }


    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {

        List<Grade> pages = gradeRepository.findAll();

        List<SubjectDTO> dtos = new ArrayList<>();
        BaseResponse baseResponse = success().build(dtos);
        return baseResponse;
    }

    @Override
    public BaseResponse findAllBySubjectCode(String subjectCode) {
        Optional<SubjectCode> optionalSubjectCode = subjectCodeService.findByCode(subjectCode);
        if(optionalSubjectCode.isEmpty()){
            return error("Invalida subject code");
        }

        Section section = optionalSubjectCode.get().getSection();

        List<Enrollment> enrollments = enrollmentService.findAllBySection(section);


        List<Grade> grades = gradeRepository.findAllBySubjectCode(optionalSubjectCode.get());
        List<GradeDTO> dos = new ArrayList<>();
        enrollments.forEach(enrollment -> {
            List<GradingPeriod> gradingPeriods = gradingPeriodRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
            for (GradingPeriod gradingPeriod : gradingPeriods){
                Optional<Grade> grade = grades.stream().filter(d-> d.getEnrollment().getId() == enrollment.getId() && d.getGradingPeriod() == gradingPeriod.getCode()).findAny();
                if(grade.isPresent()){
                    dos.add(toDTO(grade.get()));
                }else{
                  GradeDTO gradeDTO = new GradeDTO();
                  gradeDTO.setGradeScore(null);
                  gradeDTO.setGradingPeriod(gradingPeriod.getCode());
                  gradeDTO.setStudentId(enrollment.getStudent().getId());
                    dos.add(gradeDTO);

                }
            }
        });

        GradeInfoDTO gradeInfoDTO = new GradeInfoDTO();
        gradeInfoDTO.setGrades(dos);

        List<GradingPeriod> quarters = gradingPeriodRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
        List<GradingPeriodDTO> gradingPeriodDTOS = new ArrayList<>();
        quarters.stream().filter(q -> !q.isDisabled()).forEach(q -> {
            GradingPeriodDTO gradingPeriodDTO = new GradingPeriodDTO();
            gradingPeriodDTO.setCode(q.getCode());
            gradingPeriodDTO.setName(q.getName());
            gradingPeriodDTO.setActive(q.isActive());
            gradingPeriodDTOS.add(gradingPeriodDTO);
        });

        gradeInfoDTO.setQuarters(gradingPeriodDTOS);


        return success().build(gradeInfoDTO);
    }

    @Override
    public BaseResponse saveAllGrades(List<GradeDTO> grades, String subjectCode) {
        Optional<SubjectCode> optionalSubjectCode = subjectCodeService.findByCode(subjectCode);
        if(optionalSubjectCode.isEmpty()){
            return error("Invalida subject code");
        }
        List<Grade> grades1 = new ArrayList<>();

        List<GradingPeriod> quarters = gradingPeriodRepository.findAll();

        grades.forEach(d->{
            /// Save only the grades with score grea
            if(d.getGradeScore() != null && d.getId() == null && quarters.stream().anyMatch(q -> q.isActive() && d.getGradingPeriod() == q.getCode() && !q.isDisabled())) {
                Grade grade = toGrade(d);

                grade.setSubjectCode(optionalSubjectCode.get());
                Optional<Student> student = studentService.findById(d.getStudentId());
                if (student.isPresent()) {
                    Optional<Enrollment> enrollment = enrollmentService.findByStudentAndSchoolYear(student.get(), optionalSubjectCode.get().getSchoolYear());
                    enrollment.ifPresent(grade::setEnrollment);
                }

                if (grade.getEnrollment() != null) {
                    grades1.add(grade);
                }
            }
        });

        gradeRepository.saveAll(grades1);
        return findAllBySubjectCode(subjectCode);
    }

    @Override
    public List<Grade> findAllByEnrollment(Enrollment enrollment) {
        // get the final grades only
        return gradeRepository.findAllByEnrollmentAndGradingPeriod(enrollment, 4);
    }

    private GradeDTO toDTO(Grade grade){
        GradeDTO gradeDTO = new GradeDTO();
        gradeDTO.setId(grade.getId());
        gradeDTO.setGradeScore(grade.getGradeScore());
        gradeDTO.setGradingPeriod(grade.getGradingPeriod());
        gradeDTO.setStudentId(grade.getEnrollment().getStudent().getId());
        gradeDTO.setSubjectCode(grade.getSubjectCode().getCode());
        return gradeDTO;
    }

    private Grade toGrade(GradeDTO dto){
        Grade grade = new Grade();
        grade.setGradeScore(dto.getGradeScore());
        grade.setGradingPeriod(dto.getGradingPeriod());
        return grade;
    }

}
