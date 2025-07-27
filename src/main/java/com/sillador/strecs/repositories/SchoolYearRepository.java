package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.SchoolYear;
import com.sillador.strecs.entity.YearLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.Optional;

@Repository
public interface SchoolYearRepository extends JpaRepository<SchoolYear, Long> {
    Optional<SchoolYear> findByIsCurrent(boolean isCurrent);

    Optional<SchoolYear> findByYear(int year);

    Optional<SchoolYear> findTopByOrderByYearDesc();
}
