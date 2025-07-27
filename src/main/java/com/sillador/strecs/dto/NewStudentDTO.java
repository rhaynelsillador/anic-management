package com.sillador.strecs.dto;

import com.sillador.strecs.enums.EnrollmentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewStudentDTO extends StudentDTO {

    @NotNull(message = "Year level is required")
    private Long yearLevel;

    @NotNull(message = "Section is required")
    private Long section;

    @NotNull(message = "Enrollment type is required")
    private EnrollmentType enrollmentType;
}
