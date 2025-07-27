package com.sillador.strecs;


import com.sillador.strecs.entity.*;
import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import com.sillador.strecs.repositories.*;
import com.sillador.strecs.utility.CodeGenerator;
import com.sillador.strecs.utility.RandomTimeGenerator;
import com.sillador.strecs.utility.StudentIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer implements ApplicationRunner {


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

//        Long id = null;
//        for (Enrollment enrollment : enrollmentRepository.findAll()){
//            if(enrollment.getYearLevel() == null || enrollment.getSection() == null){
//                enrollment.setYearLevel(yearLevelRepository.findByName("Grade 4").get());
//                enrollment.setSection(sectionRepository.findByName("Section 4").get());
//
//                enrollmentRepository.save(enrollment);
//            }
//        }

//        studentRepository.deleteAll();
//        for (int i = 0; i < 50; i++) {
//            Student student = new Student();
//            student.setStudentId(UUID.randomUUID().toString().substring(0, 7));
//            student.setLrn(UUID.randomUUID().toString().substring(0, 10));
//            student.setFirstName(UUID.randomUUID().toString().substring(0, 10));
//            student.setLastName(UUID.randomUUID().toString().substring(0, 10));
//            int randomIndex = ThreadLocalRandom.current().nextInt(0, 2);
//            Gender gender = Gender.values()[randomIndex];
//            System.out.println("Random element: " + gender);
//            student.setGender(gender);
//            student.setBirthday(new Date(System.currentTimeMillis()));
//            student.setContactNumber(UUID.randomUUID().toString().substring(0, 11));
//
//
//            if(i < 11){
//                student.setStatus(StudentStatus.GRADUATED);
//                StudentInformation studentInformation = new StudentInformation();
//                studentInformation.setBatch(2020);
//                studentInformation.setGraduated(new Date(System.currentTimeMillis()));
//
//                studentInformation = studentInformationRepository.save(studentInformation);
//                student.setInformation(studentInformation);
//            }else if(i < 21){
//                student.setStatus(StudentStatus.ENROLLED);
//            }else if(i < 24){
//                student.setStatus(StudentStatus.INCOMPLETE);
//            }else{
//                student.setStatus(StudentStatus.OPEN);
//            }
//
//
//            studentRepository.save(student);
//        }
//
//
//        teacherRepository.deleteAll();
//        Teacher teacher = new Teacher();
//
//        teacher.setFirstName("Rhaynel");
//        teacher.setLastName("Sillador");
//        teacher.setContactNo("091234132123");
//        teacher.setPosition("Teacher 1");
//        teacher.setEmployeeNo("T1-00000011");
//
//        teacherRepository.save(teacher);
//
//        teacher = new Teacher();
//
//        teacher.setFirstName("Clarissa");
//        teacher.setLastName("Sillador");
//        teacher.setContactNo("0912341323");
//        teacher.setPosition("Teacher 2");
//        teacher.setEmployeeNo("T1-00000012");
//
//        teacherRepository.save(teacher);
//
//
//        teacher = new Teacher();
//
//        teacher.setFirstName("Clarissa1");
//        teacher.setLastName("Sillador");
//        teacher.setContactNo("0912341323");
//        teacher.setPosition("Teacher 2");
//        teacher.setEmployeeNo("T1-00000013");
//
//        teacherRepository.save(teacher);
//
//        teacher = new Teacher();
//
//        teacher.setFirstName("Clarissa2");
//        teacher.setLastName("Sillador");
//        teacher.setContactNo("0912341323");
//        teacher.setPosition("Teacher 2");
//        teacher.setEmployeeNo("T1-00000014");
//
//        teacherRepository.save(teacher);
//
//        teacher = new Teacher();
//
//        teacher.setFirstName("Clarissa3");
//        teacher.setLastName("Sillador");
//        teacher.setContactNo("0912341323");
//        teacher.setPosition("Teacher 2");
//        teacher.setEmployeeNo("T1-00000015");
//
//        teacherRepository.save(teacher);
//
//        teacher = new Teacher();
//
//        teacher.setFirstName("Clarissa4");
//        teacher.setLastName("Sillador");
//        teacher.setContactNo("0912341323");
//        teacher.setPosition("Teacher 2");
//        teacher.setEmployeeNo("T1-00000016");
//
//        teacherRepository.save(teacher);
//
//
//
//
//
//        yearLevelRepository.deleteAll();
//        subjectRepository.deleteAll();
//
//        List<String> yearLevels = List.of("Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6");
//
//        List<String> subjects = List.of("Math", "English", "Filipino", "Makabansa");
//        int currentYear = Year.now().getValue();
//        sectionRepository.deleteAll();
//        int levelOrder = 1;
//        for(String level : yearLevels){
//            YearLevel yearLevel = new YearLevel(level);
//            yearLevel.setLevelOrder(levelOrder);
//            levelOrder = levelOrder + 1;
//            Optional<YearLevel> optional =  yearLevelRepository.findByName(yearLevel.getName());
//            if(optional.isEmpty()) {
//                yearLevel = yearLevelRepository.save(yearLevel);
//
//                for(String sub : subjects) {
//                    Subject subject = new Subject();
//                    subject.setYearLevel(yearLevel);
//                    subject.setName(sub+" " + yearLevel.getName().split(" ")[1]);
//                    subject.setCode(currentYear + "-" + subject.getName().substring(0,4).replace(" ", "").toUpperCase()+yearLevel.getName().split(" ")[1]);
//
//                    if (subjectRepository.findByCode(subject.getCode()).isEmpty()) {
//                        subjectRepository.save(subject);
//                    }
//                }
//
//            }
//
//            Section section = new Section();
//            section.setCode("SECTION"+yearLevel.getName().split(" ")[1]);
//            section.setName("Section " + yearLevel.getName().split(" ")[1]);
//            section.setSchoolYear(currentYear);
//            section.setYearLevel(yearLevel);
//
//            var advisers = teacherRepository.findAll();
//
//            int randomIndex = ThreadLocalRandom.current().nextInt(advisers.size());
//            Teacher randomElement = advisers.get(randomIndex);
//            System.out.println("Random element: " + randomElement);
//
//            section.setAdviser(randomElement);
//
//            sectionRepository.save(section);
//        }
//
//
////        Section section = new Section();
////        section.setCode("RAMBUTAN");
////        section.setName("RAMBUTAN");
////        section.setSchoolYear(currentYear);
////        section.setYearLevel(yearLevelRepository.findAll().getFirst());
////        section.setAdviser(teacherRepository.findAll().getFirst());
////
////        sectionRepository.deleteAll();
////
////        sectionRepository.save(section);
////
////        section = new Section();
////        section.setCode("MANGA");
////        section.setName("MANGA");
////        section.setSchoolYear(currentYear);
////        section.setYearLevel(yearLevelRepository.findAll().getFirst());
////        section.setAdviser(teacherRepository.findAll().getLast());
////
////        sectionRepository.save(section);
//
//        subjectCodeRepository.deleteAll();
//
//        roomRepository.deleteAll();
//
//        for (int i = 1; i <= 3; i++) {
//            for (int j = 1; j <= 20; j++) {
//                Room room = new Room();
//
//                room.setBuilding("Bldg " +i);
//                room.setRoomNo("10"+j);
//
//                roomRepository.save(room);
//            }
//        }
//
//        for(Subject subject : subjectRepository.findAll()){
//            SubjectCode subjectCode = new SubjectCode();
//            subjectCode.setCode(CodeGenerator.generateCode());
//
//
//            var sections = sectionRepository.findAll();
//
//            int randomIndex = ThreadLocalRandom.current().nextInt(sections.size());
//            Section randomElement = sections.get(randomIndex);
//            System.out.println("Random element: " + randomElement);
//
//
//            subjectCode.setAdviser(randomElement.getAdviser());
//
//            subjectCode.setSection(randomElement);
//            subjectCode.setSchoolYear(currentYear);
//            subjectCode.setSubject(subject);
//            subjectCode.setRoom(roomRepository.findAll().getFirst());
//            subject.setYearLevel(randomElement.getYearLevel());
//
//            subjectCode.setStartTime(RandomTimeGenerator.getRandomTime());
//            subjectCode.setEndTime(RandomTimeGenerator.addOneHour(subjectCode.getStartTime(), 1));
//
//            subjectCodeRepository.save(subjectCode);
//        }
//
//        enrollmentRepository.deleteAll();
//
//        for (Student student : studentRepository.findAll()) {
//            // Do not enroll students who is graduated
//            if(student.getStatus() != StudentStatus.GRADUATED) {
//                Enrollment enrollment = new Enrollment();
//                enrollment.setSection(sectionRepository.findOneRandom());
//                enrollment.setEnrollmentDate(new Date(System.currentTimeMillis()));
//                enrollment.setSchoolYear(currentYear);
//                enrollment.setStudent(student);
//
//                enrollmentRepository.save(enrollment);
//            }
//        }


        for(GradingPeriod gradingPeriod : gradingPeriodRepository.findAll()){
            gradingPeriod.setActive(true);
            gradingPeriod.setDisabled(false);
            gradingPeriodRepository.save(gradingPeriod);

            System.out.println("gradingPeriod " + gradingPeriod.getCode() + " " + gradingPeriod.isActive());
        }




        // reset school year to 2025
//
//        for(YearLevel yearLevel : yearLevelRepository.findAll()){
//            System.out.println(yearLevel.getName());
//            System.out.println(yearLevel.getLevelOrder());
//        }
//
//        for(SchoolYear schoolYear : schoolYearRepository.findAll()){
//            System.out.println(schoolYear.getYear());
//            System.out.println(schoolYear.isCurrent());
//            System.out.println("++++++++++++++++++++++++++++++");
//
//        }
//
//
//        for(Student student : studentRepository.findAll()){
//            if(student.getStatus() == StudentStatus.OPEN){
//                student.setStatus(StudentStatus.ENROLLED);
//                studentRepository.save(student);
//            }
//        }
//
//        enrollmentRepository.deleteAll(enrollmentRepository.findAllBySchoolYear(2026));
//
//        Optional<SchoolYear> schoolYear = schoolYearRepository.findByYear(2025);
//        if(schoolYear.isPresent()){
//            SchoolYear year = schoolYear.get();
//            year.setCurrent(true);
//            schoolYearRepository.save(year);
//        }
//
//        for(SubjectCode subjectCode : subjectCodeRepository.findAllBySchoolYear(schoolYear.get().getYear())){
//            System.out.println("subjectCodesubjectCode : " + subjectCode.getCode());
//        }
//
//
//
//        schoolYear = schoolYearRepository.findByYear(2026);
//        if(schoolYear.isPresent()){
//
//
//
//            // Delete all create sections for school year 2026
//            for(Section section : sectionRepository.findAllBySchoolYear(schoolYear.get().getYear())){
//                sectionRepository.delete(section);
//            }
//
//            for(SubjectCode subjectCode : subjectCodeRepository.findAllBySchoolYear(schoolYear.get().getYear())){
//                subjectCodeRepository.delete(subjectCode);
//            }
//
//            schoolYearRepository.delete(schoolYear.get());
//        }



    }

}
