//package com.sillador.strecs;//package com.sillador.strecs;
//
//
//import com.sillador.strecs.entity.*;
//import com.sillador.strecs.enums.GradingPeriod;
//import com.sillador.strecs.repositories.*;
//import com.sillador.strecs.services.EnrollmentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import java.sql.Date;
//import java.time.LocalDate;
//import java.time.Year;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class GradeDataInit implements ApplicationRunner {
//
//
//    @Autowired
//    private StudentRepository studentRepository;
//    @Autowired
//    private EnrollmentService enrollmentService;
//    @Autowired
//    private GradeRepository gradeRepository;
//    @Autowired
//    private SubjectRepository subjectRepository;
//
//    @Autowired
//    private SectionRepository sectionRepository;
//
//    @Autowired
//    private SubjectCodeRepository subjectCodeRepository;
//    @Autowired
//    private EnrollmentRepository enrollmentRepository;
//    @Autowired
//    private StudentInformationRepository studentInformationRepository;
//
//
//    @Autowired
//    private RoomRepository roomRepository;
//    @Autowired
//    private SchoolYearRepository schoolYearRepository;
//    @Autowired
//    private QuarterRepository quarterRepository;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//    /**
//     * Callback used to run the bean.
//     *
//     * @param args incoming application arguments
//     * @throws Exception on error
//     */
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//
//
////        jdbcTemplate.execute("DROP TABLE IF EXISTS quarter" );
//
//        Optional<SubjectCode> subjectCode = subjectCodeRepository.findByCode("2025ICMCCYPE");
//
//        List<Enrollment> enrollments = enrollmentService.findAllBySectionAndSubject(subjectCode.get().getSection());
//
//        gradeRepository.deleteAll();
//        for (Enrollment enrollment : enrollments){
//            System.out.println(enrollment);
//
//            Grade grade = new Grade();
//            grade.setGradeScore(90);
//            grade.setGradingPeriod(GradingPeriod.FIRST.getCode());
//            grade.setRemarks("Passed");
//            grade.setEnrollment(enrollment);
//            grade.setSubjectCode(subjectCode.get());
//
//            gradeRepository.save(grade);
//        }
//
//        Optional<SchoolYear> schoolYear = schoolYearRepository.findByYear(Year.now().getValue());
//        if(schoolYear.isEmpty()){
//            SchoolYear schoolYear1 = new SchoolYear();
//            schoolYear1.setYear(Year.now().getValue());
//            String dateString = "2025-06-14";
//            LocalDate date = LocalDate.parse(dateString);
//            schoolYear1.setOpening(Date.valueOf(date));
//
//            String endString = "2026-04-30";
//            LocalDate localDate = LocalDate.parse(endString);
//            schoolYear1.setClosing(Date.valueOf(localDate));
//
//            schoolYear1.setCurrent(true);
//
//            schoolYearRepository.save(schoolYear1);
//        }
//
////        System.out.println("quarterRepository.findAll() "+ quarterRepository.findAll());
//        quarterRepository.deleteAll();
//        if(quarterRepository.findAll().isEmpty()){
//            for(GradingPeriod quarters : GradingPeriod.values()){
//                Quarter quarter = new Quarter();
//                quarter.setActive(quarters.getCode() == 1);
//                quarter.setCode(quarters.getCode());
//                quarter.setName(quarters.getName());
//                quarter.setDisabled(quarter.getCode() == 5);
//                quarterRepository.save(quarter);
//
//                System.out.println(quarter);
//            }
//        }
//
//
//
//    }
//}
