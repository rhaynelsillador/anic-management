package com.sillador.strecs.rest.controller;

import com.sillador.strecs.services.DashboardReportService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/report")
public class DashboardRestController {

    private final DashboardReportService dashboardReportService;

    public DashboardRestController(DashboardReportService dashboardReportService){
        this.dashboardReportService = dashboardReportService;
    }

    @GetMapping("/academic")
    public BaseResponse academicReport(@RequestParam(required = false) Map<String, String> query){
        return dashboardReportService.getReport(query);
    }

    @GetMapping
    public BaseResponse mainDashboard(@RequestParam(required = false) Map<String, String> query){
        return dashboardReportService.mainDashboardReport(query);
    }

}
