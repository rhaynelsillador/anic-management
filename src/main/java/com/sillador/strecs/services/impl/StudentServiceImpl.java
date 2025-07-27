package com.sillador.strecs.services.impl;

import com.sillador.strecs.config.StudentConfig;
import com.sillador.strecs.dto.StudentDTO;
import com.sillador.strecs.dto.StudentInformationDTO;
import com.sillador.strecs.entity.Student;
import com.sillador.strecs.entity.StudentInformation;
import com.sillador.strecs.repositories.StudentInformationRepository;
import com.sillador.strecs.repositories.StudentRepository;
import com.sillador.strecs.repositories.specifications.StudentSpecification;
import com.sillador.strecs.services.StudentService;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.StudentIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the StudentService interface.
 * Provides methods for managing student data, including retrieval, creation, and updates.
 */
@Service
public class StudentServiceImpl extends BaseService implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentInformationRepository studentInformationRepository;
    private final StudentConfig studentConfig;

    /**
     * Constructor for StudentServiceImpl.
     *
     * @param studentRepository the repository for managing Student entities
     */
    public StudentServiceImpl(StudentRepository studentRepository, StudentInformationRepository studentInformationRepository, StudentConfig studentConfig){
        this.studentRepository = studentRepository;
        this.studentInformationRepository = studentInformationRepository;
        this.studentConfig = studentConfig;
    }

    /**
     * Retrieves a paginated and sorted list of students based on query parameters.
     *
     * @param query a map of query parameters for filtering, sorting, and pagination
     * @return a BaseResponse containing the list of students and pagination details
     */
    @Override
    public BaseResponse getAllStudents(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));

        Sort sort = StudentSpecification.sorting(sorting);
        Specification<Student> spec = StudentSpecification.filter(query);

        System.out.println(sort + "::: " + spec);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Student> studentPage = studentRepository.findAll(spec, pageable);

        List<StudentDTO> dos = new ArrayList<>();
        studentPage.getContent().forEach(d -> dos.add(toDTO(d)));

        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(studentPage.getTotalElements(), studentPage.getSize()));
        return baseResponse;
    }

    /**
     * Finds a student by their ID.
     *
     * @param student the ID of the student to find
     * @return an Optional containing the student if found, or empty if not found
     */
    @Override
    public Optional<Student> findById(Long student) {
        return studentRepository.findById(student);
    }

    /**
     * Saves a new or existing student entity to the database.
     *
     * @param student the student entity to save
     * @return the saved student entity
     */
    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    /**
     * Creates a new student entity from a StudentDTO and saves it to the database.
     *
     * @param studentDTO the DTO containing student data
     * @return the saved student entity
     */
    @Override
    public Student newStudent(StudentDTO studentDTO) {
        Student student = toStudent(studentDTO);

        Optional<Student> latestStudent = studentRepository.findLatestStudentByStudentId();

        if(latestStudent.isPresent()){
            student.setStudentId(StudentIdGenerator.generateStudentId(studentConfig.getIdPrefix(), latestStudent.get().getStudentId()));
        }else{
            student.setStudentId(StudentIdGenerator.generateStudentId(studentConfig.getIdPrefix(), null));
        }
        return studentRepository.save(student);
    }

    /**
     * Updates an existing student entity in the database.
     *
     * @param student the student entity to update
     * @return the updated student entity
     */
    @Override
    public Student update(Student student) {
        return save(student);
    }

    @Override
    public BaseResponse getById(long id) {
        Optional<Student> studentOptional = findById(id);
        if(studentOptional.isEmpty()) {
            return error("Student does not exist");
        }
        return success().build(toDTO(studentOptional.get()));
    }

    @Override
    public BaseResponse updateStudentInfo(long id, StudentDTO studentDTO) {
        Optional<Student> studentOptional = findById(id);
        if(studentOptional.isEmpty()){
            return error("Student does not exist");
        }

        Student student = toStudent(studentDTO);
        student.setId(id);
        Student tmp = studentOptional.get();
        if(tmp.getStudentId() == null || tmp.getStudentId().isBlank()){
            tmp.setStudentId("777777");
        }
        student.setStudentId(tmp.getStudentId());
        student.setStatus(tmp.getStatus());
        student.setPhotoUrl(tmp.getPhotoUrl());

        /// Update the student information
        StudentInformation information;
        if(student.getInformation() != null) {
            information = toStudentInformation(studentDTO.getInformation());
            information.setId(student.getInformation().getId());
        }else{
            information = toStudentInformation(studentDTO.getInformation());
        }
        /// Save student information to db
        information = studentInformationRepository.save(information);
        student.setInformation(information);

        studentRepository.save(student);
        return success();
    }


    /**
     * Converts a Student entity to a StudentDTO.
     *
     * @param d the Student entity to convert
     * @return the corresponding StudentDTO
     */
    private StudentDTO toDTO(Student d){
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId(d.getStudentId());
        studentDTO.setId(d.getId());
        studentDTO.setLrn(d.getLrn());
        studentDTO.setFirstName(d.getFirstName());
        studentDTO.setLastName(d.getLastName());
        studentDTO.setMiddleName(d.getMiddleName());
        studentDTO.setBirthday(d.getBirthday());
        studentDTO.setContactNumber(d.getContactNumber());
        studentDTO.setGender(d.getGender());
        studentDTO.setStatus(d.getStatus());
        studentDTO.setEmail(d.getEmail());
        studentDTO.setAddress(d.getAddress());

        studentDTO.setFullName((d.getLastName() + ", " + d.getFirstName() + " " + d.getMiddleName())
                .replace("null", "")
                .trim());

        if(d.getInformation() != null){
            studentDTO.setInformation(toStudentInformationDTO(d.getInformation()));
        }

        return studentDTO;
    }

    private StudentInformationDTO toStudentInformationDTO(StudentInformation studentInformation){
        StudentInformationDTO studentInformationDTO = new StudentInformationDTO();
        studentInformationDTO.setBatch(studentInformation.getBatch());
        studentInformationDTO.setGraduated(studentInformation.getGraduated());
        studentInformationDTO.setGuardian(studentInformation.getGuardian());
        studentInformationDTO.setGuardianAddress(studentInformation.getGuardianAddress());
        studentInformationDTO.setGuardianContactInfo(studentInformation.getGuardianContactInfo());
        studentInformationDTO.setFatherName(studentInformation.getFatherName());
        studentInformationDTO.setMotherName(studentInformation.getMotherName());
        studentInformationDTO.setParentAddress(studentInformation.getParentAddress());
        studentInformationDTO.setParentContactInfo(studentInformation.getParentContactInfo());
        studentInformationDTO.setNationality(studentInformation.getNationality());
        studentInformationDTO.setReligion(studentInformation.getReligion());
        return studentInformationDTO;
    }


    private StudentInformation toStudentInformation(StudentInformationDTO studentInformationDTO) {
        StudentInformation studentInformation = new StudentInformation();
        studentInformation.setBatch(studentInformationDTO.getBatch());
        studentInformation.setGraduated(studentInformationDTO.getGraduated());
        studentInformation.setGuardian(studentInformationDTO.getGuardian());
        studentInformation.setGuardianAddress(studentInformationDTO.getGuardianAddress());
        studentInformation.setGuardianContactInfo(studentInformationDTO.getGuardianContactInfo());
        studentInformation.setFatherName(studentInformationDTO.getFatherName());
        studentInformation.setMotherName(studentInformationDTO.getMotherName());
        studentInformation.setParentAddress(studentInformationDTO.getParentAddress());
        studentInformation.setParentContactInfo(studentInformationDTO.getParentContactInfo());
        studentInformation.setNationality(studentInformationDTO.getNationality());
        studentInformation.setReligion(studentInformationDTO.getReligion());
        return studentInformation;
    }

    /**
     * Converts a StudentDTO to a Student entity.
     *
     * @param dto the StudentDTO to convert
     * @return the corresponding Student entity
     */
    private Student toStudent(StudentDTO dto){
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setMiddleName(dto.getMiddleName());
        student.setGender(dto.getGender());
        student.setLrn(dto.getLrn());
        student.setBirthday(dto.getBirthday());
        student.setContactNumber(dto.getContactNumber());
        student.setAddress(dto.getAddress());
        student.setEmail(dto.getEmail());
        student.setFullName((dto.getLastName()+", " + dto.getFirstName()+" " + dto.getMiddleName()).trim());
        return student;
    }
}