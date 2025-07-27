package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.TeacherDTO;
import com.sillador.strecs.services.StudentService;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher")
public class TeacherRestController {

    private final TeacherService teacherService;

    public TeacherRestController(TeacherService teacherService){
        this.teacherService = teacherService;
    }

    @GetMapping
    public BaseResponse getStudents(@RequestParam(required = false) Map<String, String> query){
        return teacherService.getAllTeachers(query);
    }

    @PostMapping
    public BaseResponse createNewTeacher(@RequestBody TeacherDTO teacherDTO){
        return  teacherService.createNewTeacher(teacherDTO);
    }

    @PutMapping("/{id}")
    public BaseResponse updateTeacher(@PathVariable long id, @RequestBody TeacherDTO teacherDTO){
        return  teacherService.updateTeacher(id, teacherDTO);
    }


}
