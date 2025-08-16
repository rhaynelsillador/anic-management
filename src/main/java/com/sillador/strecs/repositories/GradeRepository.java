package com.sillador.strecs.repositories;

import com.sillador.strecs.dto.GradeDTO;
import com.sillador.strecs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long>, JpaSpecificationExecutor<Grade> {
    List<Grade> findAllBySubjectCode(SubjectCode subjectCode);

    List<Grade> findAllByEnrollmentAndGradingPeriod(Enrollment enrollment, long gradingPeriod);
    Optional<Grade> findAllByEnrollmentAndGradingPeriodAndSubjectCode(Enrollment enrollment, long gradingPeriod, SubjectCode subjectCode);

    List<Grade> findAllByEnrollment(Enrollment enrollment);
}
