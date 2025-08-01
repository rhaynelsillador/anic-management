package com.sillador.strecs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AcademicRecordDTO {

    private EnrollmentDTO enrollment;
    private List<AcademicRecordSubjectCodeDTO> subjects;

}
