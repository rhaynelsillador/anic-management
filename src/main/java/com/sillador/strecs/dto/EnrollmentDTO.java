package com.sillador.strecs.dto;

import com.sillador.strecs.entity.Student;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class EnrollmentDTO {
    private long id;

    private String section;
    private int schoolYear;
    private Date enrollmentDate;
    private StudentDTO student;
    private String adviser;
    private String yearLevel;
    private String enrollmentType;
}
