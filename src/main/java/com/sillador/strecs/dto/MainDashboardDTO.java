package com.sillador.strecs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MainDashboardDTO {

    private List<Object[]> studentStatusCount;

    private List<Object[]> studentCountPerSchoolYear;

}
