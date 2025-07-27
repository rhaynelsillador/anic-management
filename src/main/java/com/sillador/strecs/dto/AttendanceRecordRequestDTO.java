package com.sillador.strecs.dto;

import com.sillador.strecs.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class AttendanceRecordRequestDTO {

    private long id;
    @NotNull(message = "Attendance date is required")
    private Date date;
    private String status;
    private String remarks;
    @NotNull(message = "Student id is required")
    private long studentId;
    private String subjectCode;
}
