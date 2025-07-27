package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Grade;
import com.sillador.strecs.entity.Room;
import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.entity.SubjectCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long>, JpaSpecificationExecutor<Grade> {
    List<Grade> findAllBySubjectCode(SubjectCode subjectCode);
}
