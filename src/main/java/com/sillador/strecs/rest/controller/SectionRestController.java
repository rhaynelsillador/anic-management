package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.SectionDTO;
import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.services.SubjectService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/section")
public class SectionRestController {

    private final SectionService sectionService;

    public SectionRestController(SectionService sectionService){
        this.sectionService = sectionService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return sectionService.getAll(query);
    }

    @PostMapping
    public BaseResponse create(@Valid @RequestBody SectionDTO dto){
        return sectionService.create(dto);
    }

    @PutMapping("/{id}")
    public BaseResponse update(@PathVariable Long id, @Valid @RequestBody SectionDTO dto){
        return sectionService.update(id, dto);
    }
}
