package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.InitSchoolYearDTO;
import com.sillador.strecs.dto.SchoolYearDTO;
import com.sillador.strecs.services.SchoolYearService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/schoolyear")
public class SchoolYearRestController {

    private final SchoolYearService schoolYearService;

    public SchoolYearRestController(SchoolYearService schoolYearService){
        this.schoolYearService = schoolYearService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return schoolYearService.getAll(query);
    }

    @PostMapping
    public BaseResponse createNewSchoolYear(@RequestBody SchoolYearDTO schoolYearDTO){
        return schoolYearService.createNewSchoolYear(schoolYearDTO);
    }

    @PutMapping("/{id}")
    public BaseResponse updateSchoolYear(@PathVariable long id, @RequestBody SchoolYearDTO schoolYearDTO){
        return schoolYearService.updateSchoolYear(id, schoolYearDTO);
    }


    @PostMapping("/initialize")
    public BaseResponse initializedNewSchoolYear(@RequestBody InitSchoolYearDTO initSchoolYearDTO){
        return schoolYearService.openNewSchoolYear(initSchoolYearDTO);
    }

}
