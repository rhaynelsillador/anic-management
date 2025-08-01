package com.sillador.strecs.services;

import com.sillador.strecs.dto.GradeDTO;
import com.sillador.strecs.dto.SubjectCodeDTO;
import com.sillador.strecs.entity.Enrollment;
import com.sillador.strecs.entity.Grade;
import com.sillador.strecs.entity.Room;
import com.sillador.strecs.utility.BaseResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GradeService {

    BaseResponse getAll(Map<String, String> query);

    BaseResponse findAllBySubjectCode(String subjectCode);

    BaseResponse saveAllGrades(List<GradeDTO> grades, String subjectCode);

    List<Grade> findAllByEnrollment(Enrollment enrollment);

}
