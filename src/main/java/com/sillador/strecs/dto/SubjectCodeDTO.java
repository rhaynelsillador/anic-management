package com.sillador.strecs.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;

@Setter
@Getter
public class SubjectCodeDTO {

    private long id;
    private String code;
    private String yearLevel;
    private String subjectName;
    private String subjectCode;
    private String adviser;
    private String room;
    private int schoolYear;

    private String section;

    private boolean active;
    private boolean locked;

    private Time startTime;
    private Time endTime;

    private int units;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
