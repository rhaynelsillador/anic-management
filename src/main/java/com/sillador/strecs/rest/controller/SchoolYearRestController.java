package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.SchoolYearDTO;
import com.sillador.strecs.services.SchoolYearService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schoolyear")
public class SchoolYearRestController {

    private final SchoolYearService schoolYearService;

    public SchoolYearRestController(SchoolYearService schoolYearService){
        this.schoolYearService = schoolYearService;
    }

    @PostMapping
    public BaseResponse initializedNewSchoolYear(@RequestBody SchoolYearDTO schoolYearDTO){
        return schoolYearService.openNewSchoolYear(schoolYearDTO);
    }

}
