package com.sillador.strecs.services;

import com.sillador.strecs.dto.TeacherDTO;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.utility.BaseResponse;

import java.util.Map;
import java.util.Optional;

public interface TeacherService {

    BaseResponse getAllTeachers(Map<String, String> query);

    Optional<Teacher> findById(long id);

    BaseResponse createNewTeacher(TeacherDTO teacherDTO);

    BaseResponse updateTeacher(long id, TeacherDTO teacherDTO);
}
