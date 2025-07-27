package com.sillador.strecs.services;

import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public interface YearLevelService {

    BaseResponse getAll(Map<String, String> query);

    Optional<YearLevel> findById(@NotNull(message = "Grade Level is required") Long yearLevel);
}
