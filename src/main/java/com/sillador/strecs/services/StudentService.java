package com.sillador.strecs.services;

import com.sillador.strecs.dto.NewStudentDTO;
import com.sillador.strecs.dto.StudentDTO;
import com.sillador.strecs.entity.Student;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {

    BaseResponse getAllStudents(Map<String, String> query);


    Optional<Student> findById(@NotNull(message = "Student is required") Long student);

    Student save(@NotNull Student student);

    Student newStudent(@Valid @NotNull StudentDTO studentDTO);

    Student update(Student student);

    BaseResponse getById(long id);

    BaseResponse updateStudentInfo(long id, StudentDTO studentDTO);
}
