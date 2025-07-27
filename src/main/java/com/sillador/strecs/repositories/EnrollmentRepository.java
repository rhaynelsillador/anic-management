package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {

    Optional<Enrollment> findByStudentAndSchoolYear(Student student1, int currentYear);

    List<Enrollment> findAllBySection(Section section);

    Optional<Enrollment> findByStudent(Student student);

    List<Enrollment> findAllBySchoolYear(int year);

    @Query(value = "SELECT gender, COUNT(*), y.name FROM student s inner join enrollments e on e.student_id = s.id inner join year_levels y on e.year_level_id = y.id where s.status = 0 and e.school_year = ?1 GROUP BY gender, y.id", nativeQuery = true)
    List<Object[]> countByGenderNative(int schoolYear);

    @Query(value = "SELECT y.name, COUNT(*) FROM student s inner join enrollments e on e.student_id = s.id inner join year_levels y on e.year_level_id = y.id where s.status = 0 and e.school_year = ?1 GROUP BY y.id", nativeQuery = true)
    List<Object[]> countStudentByGradeLevelNative(int schoolYear);

    @Query(value = "SELECT s.status, COUNT(*) FROM student s inner join enrollments e on e.student_id = s.id where e.school_year = ?1 GROUP BY s.status", nativeQuery = true)
    List<Object[]> countStudentByStatusNative(int schoolYear);

    @Query(value = "SELECT e.enrollment_type, COUNT(*) FROM enrollments e where e.school_year = ?1 GROUP BY e.enrollment_type", nativeQuery = true)
    List<Object[]> countByEnrollmentTypeNative(int schoolYear);



}
