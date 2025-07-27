package com.sillador.strecs.rest.controller;

import com.sillador.strecs.dto.AttendanceRecordDTO;
import com.sillador.strecs.dto.AttendanceRecordRequestDTO;
import com.sillador.strecs.dto.SectionDTO;
import com.sillador.strecs.services.AttendanceRecordService;
import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceRecordRestController {

    private final AttendanceRecordService attendanceRecordService;

    public AttendanceRecordRestController(AttendanceRecordService attendanceRecordService){
        this.attendanceRecordService = attendanceRecordService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query) throws ParseException {
        return attendanceRecordService.getClassAttendance(query);
    }

    @PostMapping
    public BaseResponse createAttendanceRecord(@Valid @RequestBody AttendanceRecordRequestDTO attendanceRecordDTO) {
        // This method should handle the creation of attendance records based on the provided SectionDTO
        // Implementation details would depend on the specific requirements and logic of your application
        return attendanceRecordService.createAttendanceRecord(attendanceRecordDTO);
    }
}
