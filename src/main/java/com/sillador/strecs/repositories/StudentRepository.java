package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query(value = "SELECT * FROM student ORDER BY student_id DESC LIMIT 1", nativeQuery = true)
    Optional<Student> findLatestStudentByStudentId();

}
