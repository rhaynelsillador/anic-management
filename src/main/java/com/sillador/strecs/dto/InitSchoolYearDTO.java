package com.sillador.strecs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class InitSchoolYearDTO {

    private int schoolYearId;
    private java.sql.Date graduationDate;
    private int batch;
}
