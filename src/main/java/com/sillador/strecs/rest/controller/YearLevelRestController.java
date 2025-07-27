package com.sillador.strecs.rest.controller;

import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/yearlevel")
public class YearLevelRestController {

    private final YearLevelService yearLevelService;

    public YearLevelRestController(YearLevelService yearLevelService){
        this.yearLevelService = yearLevelService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return yearLevelService.getAll(query);
    }


}
