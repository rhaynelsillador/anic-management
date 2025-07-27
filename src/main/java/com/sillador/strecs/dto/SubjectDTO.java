package com.sillador.strecs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubjectDTO {

    private long id;
    @NotNull(message = "Subject code is required")
    private String code;
    @NotNull(message = "Subject name is required")
    private String name;
    @NotNull(message = "Subject year level is required")
    private String yearLevel;
}
