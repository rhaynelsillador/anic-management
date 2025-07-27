package com.sillador.strecs.services;

import com.sillador.strecs.dto.SubjectCodeRequestDTO;
import com.sillador.strecs.entity.SubjectCode;
import com.sillador.strecs.utility.BaseResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubjectCodeService {

    BaseResponse getAll(Map<String, String> query);

    SubjectCode create(SubjectCode subjectCode);

    BaseResponse save(SubjectCodeRequestDTO subjectCodeRequest);

    BaseResponse getSubjectsByAdviser(Long adviserId, long schoolYear);

    BaseResponse getStudentsBySubjectCode(String code);

    Optional<SubjectCode> findByCode(String subjectCode);
}
