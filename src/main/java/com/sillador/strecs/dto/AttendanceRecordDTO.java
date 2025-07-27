package com.sillador.strecs.dto;

import com.sillador.strecs.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class AttendanceRecordDTO {

    private long id;
    private Date date;
    private AttendanceStatus status;
    private String remarks;
    private long studentId;
    private String studentName;
}
