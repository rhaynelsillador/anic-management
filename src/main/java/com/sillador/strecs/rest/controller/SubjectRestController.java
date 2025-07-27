package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.SubjectDTO;
import com.sillador.strecs.services.SubjectService;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subject")
public class SubjectRestController {

    private final SubjectService subjectService;

    public SubjectRestController(SubjectService subjectService){
        this.subjectService = subjectService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return subjectService.getAll(query);
    }

    @PostMapping
    public BaseResponse createSubject(@RequestBody SubjectDTO subjectDTO){
        return subjectService.createSubject(subjectDTO);
    }

    @PutMapping("/{id}")
    public BaseResponse createSubject(@PathVariable long id, @RequestBody SubjectDTO subjectDTO){
        return subjectService.updateSubject(id, subjectDTO);
    }
}
