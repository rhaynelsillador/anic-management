package com.sillador.strecs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SectionDTO {

    private long id;
    @NotNull(message = "Section code is required")
    private String code;
    @NotNull(message = "Section name is required")
    private String name;
    private String yearLevel;
    @NotNull(message = "Grade level is required")
    private Long yearLevelId;
    private String adviser;
//    @NotNull(message = "Adviser is required")
    private Long adviserId;

    private int schoolYear;
}
