package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Section;
import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.entity.SubjectCode;
import com.sillador.strecs.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectCodeRepository extends JpaRepository<SubjectCode, Long>, JpaSpecificationExecutor<SubjectCode> {
    Optional<SubjectCode> findByCode(String code);

    List<SubjectCode> findAllBySection(Section section);

    Optional<SubjectCode> findBySectionAndSubjectAndSchoolYear(Section section, Subject subject, int schoolYear);

    List<SubjectCode> findAllByAdviserAndSchoolYear(Teacher teacher, long schoolYear);

    List<SubjectCode>  findAllBySchoolYear(int year);

    Optional<SubjectCode> findByCodeAndSectionAndSubjectAndSchoolYear(String code, Section section, Subject subject, int schoolYear);
}
