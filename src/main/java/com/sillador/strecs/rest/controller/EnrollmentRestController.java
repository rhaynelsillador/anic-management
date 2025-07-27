package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.NewStudentDTO;
import com.sillador.strecs.dto.StudentLevelAssignmetDTO;
import com.sillador.strecs.services.EnrollmentService;
import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/enrollment")
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;

    public EnrollmentRestController(EnrollmentService enrollmentService){
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return enrollmentService.getAll(query);
    }

    @PostMapping
    public BaseResponse enrollStudents(@Valid  @RequestBody List<StudentLevelAssignmetDTO> dtos){
        return  enrollmentService.enrollStudents(dtos);
    }

    @PostMapping("/new")
    public BaseResponse enrollStudent(@Valid  @RequestBody NewStudentDTO dto){
        return  enrollmentService.enrollStudent(dto);
    }


}
