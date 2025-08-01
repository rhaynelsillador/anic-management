package com.sillador.strecs;


import com.sillador.strecs.entity.GradingPeriod;
import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.enums.GroupYearLevel;
import com.sillador.strecs.repositories.*;
import com.sillador.strecs.utility.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataInitializerV2 implements ApplicationRunner {


    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private YearLevelRepository yearLevelRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectCodeRepository subjectCodeRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private StudentInformationRepository studentInformationRepository;


    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GradingPeriodRepository gradingPeriodRepository;

    @Autowired
    private SchoolYearRepository schoolYearRepository;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        for(YearLevel yearLevel : yearLevelRepository.findAll()){
            if(yearLevel.getCode() == null) {
                yearLevel.setCode(CodeGenerator.generateCode());
                if (yearLevel.getLevelOrder() > 1) {
                    Optional<YearLevel> pre = yearLevelRepository.findByLevelOrderAndGroupYearLevel(yearLevel.getLevelOrder() - 1, GroupYearLevel.ELEMENTARY);
                    pre.ifPresent(yearLevel::setPrerequisiteYear);
                }
                yearLevel.setGroupYearLevel(GroupYearLevel.ELEMENTARY);

                yearLevelRepository.save(yearLevel);
            }

            if(yearLevel.getGroupYearLevel() == null){
                yearLevel.setGroupYearLevel(GroupYearLevel.JUNIOR_HIGH);
                yearLevelRepository.save(yearLevel);
            }

        }
//
//        List<String> juniorHi = List.of("Grade 7", "Grade 8", "Grade 9", "Grade 10");
//
//
//        int order = 1;
//        for(String s : juniorHi) {
//
//            YearLevel yearLevel = new YearLevel();
//
//            yearLevel.setCode(CodeGenerator.generateCode());
//            if (order > 1) {
//                Optional<YearLevel> pre = yearLevelRepository.findByLevelOrderAndGroupYearLevel(yearLevel.getLevelOrder() - 1, GroupYearLevel.JUNIOR_HIGH);
//                pre.ifPresent(yearLevel::setPrerequisiteYear);
//            }
//            yearLevel.setName(s);
//            yearLevel.setLevelOrder(order);
//
//            yearLevelRepository.save(yearLevel);
//
//            order++;
//        }

    }

}
