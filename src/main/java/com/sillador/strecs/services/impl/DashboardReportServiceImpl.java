package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.AcademicReportDTO;
import com.sillador.strecs.dto.MainDashboardDTO;
import com.sillador.strecs.entity.SchoolYear;
import com.sillador.strecs.repositories.EnrollmentRepository;
import com.sillador.strecs.repositories.SchoolYearRepository;
import com.sillador.strecs.repositories.TeacherRepository;
import com.sillador.strecs.services.DashboardReportService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardReportServiceImpl extends BaseService implements DashboardReportService {

    private EnrollmentRepository enrollmentRepository;
    private SchoolYearRepository schoolYearRepository;
    private TeacherRepository teacherRepository;

    public DashboardReportServiceImpl(EnrollmentRepository enrollmentRepository, SchoolYearRepository schoolYearRepository, TeacherRepository teacherRepository){
        this.enrollmentRepository = enrollmentRepository;
        this.schoolYearRepository = schoolYearRepository;
        this.teacherRepository = teacherRepository;
    }


    @Override
    public BaseResponse getReport(Map<String, String> query) {
        SchoolYear schoolYear = getSchoolYear(query);
        if(schoolYear == null){
            return error("No record found");
        }

        AcademicReportDTO reportDTO = new AcademicReportDTO();

        List<Object[]> genders = enrollmentRepository.countByGenderNative(schoolYear.getYear());

        List<Object[]> studentCount = enrollmentRepository.countStudentByGradeLevelNative(schoolYear.getYear());

        reportDTO.setStudentStatusCount(enrollmentRepository.countStudentByStatusNative(schoolYear.getYear()));

        reportDTO.setEnrollmentType(enrollmentRepository.countByEnrollmentTypeNative(schoolYear.getYear()));
        reportDTO.setTotalTeachers(teacherRepository.count());


        reportDTO.setGender(genders);
        reportDTO.setStudentCount(studentCount);

        return success().build(reportDTO);
    }

    @Override
    public BaseResponse mainDashboardReport(Map<String, String> query) {
        SchoolYear schoolYear = getSchoolYear(query);
        if(schoolYear == null){
            return error("No record found");
        }

        MainDashboardDTO mainDashboardDTO = new MainDashboardDTO();

        mainDashboardDTO.setStudentStatusCount(enrollmentRepository.countStudentByStatusNative(schoolYear.getYear()));


        return success().build(mainDashboardDTO);
    }

    private SchoolYear getSchoolYear(Map<String, String> query){
        SchoolYear schoolYear = null;
        try{
            int schoolYearInt = Integer.parseInt(query.get("schoolYear"));
            Optional<SchoolYear> schoolYearOptional = schoolYearRepository.findByYear(schoolYearInt);
            if(schoolYearOptional.isPresent()){
                schoolYear = schoolYearOptional.get();
            }
        }catch (Exception ignored){
            Optional<SchoolYear> schoolYearOptional = schoolYearRepository.findByIsCurrent(true);
            if(schoolYearOptional.isPresent()){
                schoolYear = schoolYearOptional.get();
            }
        }

        return schoolYear;
    }
}
