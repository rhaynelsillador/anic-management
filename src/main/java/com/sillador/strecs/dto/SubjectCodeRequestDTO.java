package com.sillador.strecs.dto;

import com.sillador.strecs.entity.SubjectCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class SubjectCodeRequestDTO {

    @NotNull(message = "Section is required")
    private Long section;
    @NotEmpty(message = "Subjects list must not be empty")
    @Valid
    private  List<SubjectCodeDTO> subjects;

    @Override
    public String toString() {
        return "SubjectCodeRequestDTO{" +
                "section=" + section +
                ", subjects=" + subjects +
                '}';
    }
}
