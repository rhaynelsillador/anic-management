package com.sillador.strecs.services;

import com.sillador.strecs.dto.EnrollmentDTO;
import com.sillador.strecs.dto.NewStudentDTO;
import com.sillador.strecs.dto.StudentLevelAssignmetDTO;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EnrollmentService {

    BaseResponse getAll(Map<String, String> query);

    BaseResponse enrollStudents(@Valid List<StudentLevelAssignmetDTO> dtos);


    BaseResponse enrollStudent(@Valid NewStudentDTO studentDTO);

    List<Enrollment> findAllBySection(Section section);

    EnrollmentDTO toDTO(Enrollment d, boolean includeStudent);

    Optional<Enrollment> findByStudentAndSchoolYear(Student student, int year);

    List<Enrollment> findAllEnrolledStudents(SchoolYear oldSchoolYear);

    Enrollment save(Enrollment enrollment);

    List<Enrollment> findAllByStudentOrderBySchoolYearAsc(Student student);

    void deleteAll(List<Enrollment> enrollmentList2);
}
