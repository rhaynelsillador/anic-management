package com.sillador.strecs.services;

import com.sillador.strecs.utility.BaseResponse;

import java.util.Map;

public interface DashboardReportService {
    BaseResponse getReport(Map<String, String> query);

    BaseResponse mainDashboardReport(Map<String, String> query);
}
