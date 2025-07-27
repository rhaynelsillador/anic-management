package com.sillador.strecs.services;

import com.sillador.strecs.dto.SectionDTO;
import com.sillador.strecs.entity.Section;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public interface SectionService {

    BaseResponse getAll(Map<String, String> query);

    Optional<Section> findById(@NotNull(message = "Section is required") Long section);

    BaseResponse update(@NotNull Long id, @Valid SectionDTO dto);

    BaseResponse create(@Valid SectionDTO dto);
}
