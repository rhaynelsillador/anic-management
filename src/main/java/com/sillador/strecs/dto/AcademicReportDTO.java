package com.sillador.strecs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class AcademicReportDTO {

    private List<Object[]> gender;
    private List<Object[]> studentCount;
    private List<Object[]> studentStatusCount;
    private List<Object[]> enrollmentType;

    private long totalTeachers;

}
