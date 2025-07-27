package com.sillador.strecs.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

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

    private Time startTime;
    private Time endTime;

}
