package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.GradeDTO;
import com.sillador.strecs.dto.SubjectCodeRequestDTO;
import com.sillador.strecs.services.GradeService;
import com.sillador.strecs.services.SubjectCodeService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/grade")
public class GradeRestController {

    private final GradeService gradeService;

    public GradeRestController(GradeService gradeService){
        this.gradeService = gradeService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return gradeService.getAll(query);
    }

    @GetMapping("/code/{code}")
    public BaseResponse getSubjectsByAdviser(@PathVariable String code){
        return  gradeService.findAllBySubjectCode(code);
    }

    @PostMapping("/code/{code}")
    public BaseResponse saveGrades(@PathVariable String code, @RequestBody List<GradeDTO> grades){
        return  gradeService.saveAllGrades(grades, code);
    }
}
