package com.sillador.strecs.dto;

import com.sillador.strecs.enums.GroupYearLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AcademicRecordSubjectCodeDTO {

    private String subjectCode;
    private String subjectName;
    private int units;
    private String adviser;
    private String roomNum;
    private float grades;
    private String status; /// Passed, Failed, Incomplete
    private String remarks;

    private GroupYearLevel groupYearLevel;
}
