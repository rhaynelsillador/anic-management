package com.sillador.strecs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class GradeInfoDTO {

    List<GradeDTO> grades;
    List<GradingPeriodDTO>  quarters;

}
