package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.AttendanceRecordDTO;
import com.sillador.strecs.dto.AttendanceRecordRequestDTO;
import com.sillador.strecs.dto.EnrollmentDTO;
import com.sillador.strecs.entity.AttendanceRecord;
import com.sillador.strecs.entity.Enrollment;
import com.sillador.strecs.entity.Student;
import com.sillador.strecs.entity.SubjectCode;
import com.sillador.strecs.enums.AttendanceStatus;
import com.sillador.strecs.repositories.AttendanceRecordRepository;
import com.sillador.strecs.repositories.specifications.AttendanceRecordSpecification;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.EnrollmentSpecification;
import com.sillador.strecs.services.*;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttendanceRecordServiceImpl extends BaseService implements AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final SubjectCodeService subjectCodeService;

    public AttendanceRecordServiceImpl(AttendanceRecordRepository attendanceRecordRepository, EnrollmentService enrollmentService, StudentService studentService, SubjectService subjectService, SubjectCodeService subjectCodeService) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.subjectCodeService = subjectCodeService;
    }

    @Override
    public BaseResponse getClassAttendance(Map<String, String> query) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(query.get("startDate"), formatter);
        LocalDate endDate = LocalDate.parse(query.get("endDate"), formatter);

        // Step 2: Convert LocalDate to java.sql.Date
        Date sqlDateStart = java.sql.Date.valueOf(startDate);
        Date sqlDateEnd = java.sql.Date.valueOf(endDate);


        List<AttendanceRecord> pages = attendanceRecordRepository.findAllBySubjectCodeCodeAndDateBetween(
                query.get("code"),
                sqlDateStart,
                sqlDateEnd
        );

        List<AttendanceRecordDTO> dos = new ArrayList<>();
        pages.forEach(d -> {
            AttendanceRecordDTO dto = new AttendanceRecordDTO();
            dto.setId(d.getId());
            dto.setStudentName(d.getStudent().getFullNameStr());
            dto.setStudentId(d.getStudent().getId());
            dto.setDate(d.getDate());
            dto.setStatus(d.getStatus());
            dto.setRemarks(d.getRemarks());
            dos.add(dto);
        });

        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.size(), 0));
        return baseResponse;
    }

    @Override
    public Optional<AttendanceRecord> findById(long id) {
        return attendanceRecordRepository.findById(id);
    }

    @Override
    public BaseResponse createAttendanceRecord(AttendanceRecordRequestDTO attendanceRecordDTO) {
        Optional<Student> student = studentService.findById(attendanceRecordDTO.getStudentId());
        if(student.isEmpty()){
            return error("Invalid student request");
        }



        Optional<SubjectCode> subjectCode = subjectCodeService.findByCode(attendanceRecordDTO.getSubjectCode());
        if (subjectCode.isEmpty()){
            return error("Invalid subject code request");
        }

        Optional<Enrollment> enrollment = enrollmentService.findByStudentAndSchoolYear(
                student.get(),
                subjectCode.get().getSchoolYear()
        );
        if (enrollment.isEmpty()){
            return error("Invalid enrollment request");
        }

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setDate(attendanceRecordDTO.getDate());
        attendanceRecord.setRemarks(attendanceRecordDTO.getRemarks());
        attendanceRecord.setSection(enrollment.get().getSection());
        attendanceRecord.setSubjectCode(subjectCode.get());

        attendanceRecord.setStudent(student.get());

        attendanceRecord.setStatus(AttendanceStatus.valueOf(attendanceRecordDTO.getStatus()));
        attendanceRecord = attendanceRecordRepository.save(attendanceRecord);
        System.out.println("Attendance Record Created: " + attendanceRecord.getId() + " status : " + attendanceRecord.getStatus());
        return success("Successfully created attendance record");
    }
}
