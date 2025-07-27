package com.sillador.strecs.repositories;

import com.sillador.strecs.entity.AttendanceRecord;
import com.sillador.strecs.entity.Enrollment;
import com.sillador.strecs.entity.Section;
import com.sillador.strecs.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {

    List<AttendanceRecord> findAllBySubjectCodeCodeAndDateBetween(String code, Date startDate, Date endDate);
}
