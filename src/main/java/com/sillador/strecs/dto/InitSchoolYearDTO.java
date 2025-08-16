package com.sillador.strecs.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class SchoolYearDTO {

    private long id;
    @NotNull(message = "School year is required")
    private Integer year;
    @NotNull(message = "Opening date is required")
    private java.sql.Date opening;
    private java.sql.Date closing;
    private boolean isCurrent;
    private Timestamp createdDate;
    private Timestamp updatedDate;
}
