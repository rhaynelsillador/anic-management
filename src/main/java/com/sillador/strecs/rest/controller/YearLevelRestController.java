package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.YearLevelDTO;
import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public BaseResponse createNewClass(@RequestBody YearLevelDTO yearLevelDTO){
        return yearLevelService.createNewClass(yearLevelDTO);
    }

    @PutMapping("/{id}")
    public BaseResponse updateClass(@PathVariable long id, @RequestBody YearLevelDTO yearLevelDTO){
        return yearLevelService.updateClass(id, yearLevelDTO);
    }


}
