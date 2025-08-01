package com.sillador.strecs.dto;

import com.sillador.strecs.enums.GroupYearLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class YearLevelDTO {

    private long id;
    @NotNull(message = "Class name is required")
    private String name;
    private String code; /// this can be used as a course code

    private YearLevelDTO prerequisiteYear;

    @NotNull(message = "Class group type is required")
    private GroupYearLevel groupYearLevel;

    private boolean lastLevel;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
