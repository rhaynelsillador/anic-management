package com.sillador.strecs.rest.controller;

import com.sillador.strecs.services.AppConfigService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/config")
public class AppConfigRestController {

    private final AppConfigService appConfigService;

    public AppConfigRestController(AppConfigService appConfigService){
        this.appConfigService = appConfigService;
    }

    @GetMapping
    public BaseResponse getAppConfig(){
        return appConfigService.getAppConfig();
    }
}
