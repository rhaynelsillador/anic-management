package com.sillador.strecs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentLevelAssignmetDTO {

    @NotNull(message = "Student is required")
    private Long student;
    @NotNull(message = "Grade level is required")
    private Long yearLevel;
    @NotNull(message = "Section is required")
    private Long section;


}
