package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.entity.YearLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {
    Optional<Subject> findByCode(String code);

    List<Subject> findAllByYearLevel(YearLevel yearLevel);
}
