package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Section;
import com.sillador.strecs.entity.YearLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long>, JpaSpecificationExecutor<Section> {
    Optional<Section> findByName(String name);

    @Query(value = "SELECT * FROM sections ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Section findOneRandom();

    List<Section> findAllBySchoolYear(int year);

    Optional<Section> findByCodeAndYearLevelAndSchoolYear(String code, YearLevel yearLevel, int year);
}
