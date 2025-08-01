package com.sillador.strecs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private boolean active;
    private int units;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
