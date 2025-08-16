package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Student;
import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.enums.GroupYearLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YearLevelRepository extends JpaRepository<YearLevel, Long>, JpaSpecificationExecutor<YearLevel> {
    Optional<YearLevel> findByName(String name);

    @Query(value = "SELECT * FROM year_levels ORDER BY level_order DESC LIMIT 1", nativeQuery = true)
    Optional<YearLevel> findLastYearLevelByLevelOrder();

    Optional<YearLevel> findByLevelOrder(int yearLevelOrder);

    Optional<YearLevel> findByLevelOrderAndGroupYearLevel(int i, GroupYearLevel groupYearLevel);

    Optional<YearLevel> findByPrerequisiteYear(YearLevel yearLevel);
}
