package com.sillador.strecs.services;

import com.sillador.strecs.dto.SubjectDTO;
import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.utility.BaseResponse;

import java.util.Map;
import java.util.Optional;

public interface SubjectService {

    BaseResponse getAll(Map<String, String> query);

    Optional<Subject> findByCode(String subjectCode);

    BaseResponse createSubject(SubjectDTO subjectDTO);

    BaseResponse updateSubject(long id, SubjectDTO subjectDTO);
}
