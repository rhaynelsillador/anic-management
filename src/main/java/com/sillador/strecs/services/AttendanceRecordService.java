package com.sillador.strecs.services;

import com.sillador.strecs.dto.AttendanceRecordDTO;
import com.sillador.strecs.dto.AttendanceRecordRequestDTO;
import com.sillador.strecs.entity.AttendanceRecord;
import com.sillador.strecs.entity.Room;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

public interface AttendanceRecordService {

    BaseResponse getClassAttendance(Map<String, String> query) throws ParseException;

    Optional<AttendanceRecord> findById(long id);

    BaseResponse createAttendanceRecord(@Valid AttendanceRecordRequestDTO attendanceRecordDTO);
}
