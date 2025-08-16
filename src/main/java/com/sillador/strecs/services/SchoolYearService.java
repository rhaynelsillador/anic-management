package com.sillador.strecs.services;

import com.sillador.strecs.dto.InitSchoolYearDTO;
import com.sillador.strecs.dto.SchoolYearDTO;
import com.sillador.strecs.entity.SchoolYear;
import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.services.impl.BaseService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public interface SchoolYearService {

    BaseResponse getAll(Map<String, String> query);

    Optional<SchoolYear> findById(@NotNull(message = "Grade Level is required") Long schoolYear);

    BaseResponse createNewSchoolYear(SchoolYearDTO schoolYearDTO);

    BaseResponse updateSchoolYear(long id, SchoolYearDTO schoolYearDTO);

    BaseResponse openNewSchoolYear(InitSchoolYearDTO initSchoolYearDTO);
}
