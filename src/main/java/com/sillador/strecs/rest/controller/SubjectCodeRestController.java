package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.SubjectCodeRequestDTO;
import com.sillador.strecs.services.SubjectCodeService;
import com.sillador.strecs.services.SubjectService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subjectcode")
public class SubjectCodeRestController {

    private final SubjectCodeService subjectCodeService;

    public SubjectCodeRestController(SubjectCodeService subjectCodeService){
        this.subjectCodeService = subjectCodeService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return subjectCodeService.getAll(query);
    }

    @PostMapping
    public BaseResponse save(@Valid @RequestBody SubjectCodeRequestDTO subjectCodeRequest){
        return subjectCodeService.save(subjectCodeRequest);
    }

    @GetMapping("/adviser/{adviserId}/year/{schoolYear}")
    public BaseResponse getSubjectsByAdviser(@PathVariable Long adviserId, @PathVariable Long schoolYear){
        return  subjectCodeService.getSubjectsByAdviser(adviserId, schoolYear);
    }

    @GetMapping("/code/{code}")
    public BaseResponse getSubjectsByAdviser(@PathVariable String code){
        return  subjectCodeService.getStudentsBySubjectCode(code);
    }
}
