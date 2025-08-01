package com.sillador.strecs.dto;

import com.sillador.strecs.enums.GradingPeriod;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GradeDTO {

    private Long id;
    private Long studentId;
    private Float gradeScore;
    private Integer gradingPeriod;
    private String subjectCode;

}
