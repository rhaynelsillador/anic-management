package com.sillador.strecs.services;

import com.sillador.strecs.dto.GradeDTO;
import com.sillador.strecs.entity.Room;
import com.sillador.strecs.utility.BaseResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GradeService {

    BaseResponse getAll(Map<String, String> query);

    BaseResponse findAllBySubjectCode(String subjectCode);

    BaseResponse saveAllGrades(List<GradeDTO> grades, String subjectCode);
}
