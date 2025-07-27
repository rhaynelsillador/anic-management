package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.NewStudentDTO;
import com.sillador.strecs.dto.StudentDTO;
import com.sillador.strecs.entity.Student;
import com.sillador.strecs.services.StudentService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping
    public BaseResponse getStudents(@RequestParam(required = false) Map<String, String> query){
        return studentService.getAllStudents(query);
    }

    @GetMapping("/{id}")
    public BaseResponse getStudentById(@PathVariable long id){
        return studentService.getById(id);
    }

    @PutMapping("/{id}")
    public BaseResponse updateStudentInfo(@PathVariable long id, @RequestBody StudentDTO studentDTO){
        return studentService.updateStudentInfo(id, studentDTO);
    }

}
