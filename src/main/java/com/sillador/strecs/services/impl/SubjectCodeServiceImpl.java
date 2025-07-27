package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.EnrollmentDTO;
import com.sillador.strecs.dto.SectionDTO;
import com.sillador.strecs.dto.SubjectCodeDTO;
import com.sillador.strecs.dto.SubjectCodeRequestDTO;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.repositories.SchoolYearRepository;
import com.sillador.strecs.repositories.SectionRepository;
import com.sillador.strecs.repositories.SubjectCodeRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.SectionSpecification;
import com.sillador.strecs.repositories.specifications.SubjectCodeSpecification;
import com.sillador.strecs.services.*;
import com.sillador.strecs.utility.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SubjectCodeServiceImpl extends BaseService implements SubjectCodeService {

    private final SubjectCodeRepository subjectCodeRepository;
    private final SubjectService subjectService;
    private final RoomService roomService;
    private final SectionService sectionService;
    private final TeacherService teacherService;
    private final EnrollmentService enrollmentService;

    @Autowired
    private SchoolYearRepository schoolYearRepository;

    public SubjectCodeServiceImpl(SubjectCodeRepository subjectCodeRepository, SubjectService subjectService,  RoomService roomService, SectionService sectionService, TeacherService teacherService, EnrollmentService enrollmentService){
        this.subjectCodeRepository = subjectCodeRepository;
        this.subjectService = subjectService;
        this.roomService = roomService;
        this.sectionService = sectionService;
        this.teacherService = teacherService;
        this.enrollmentService = enrollmentService;
    }


    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));


        Sort sort = SubjectCodeSpecification.sorting(sorting, SubjectCodeSpecification.getSortedKeys());
        Specification<SubjectCode> spec = SubjectCodeSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<SubjectCode> pages = subjectCodeRepository.findAll(spec, pageable);

        List<SubjectCodeDTO> dos = new ArrayList<>();
        pages.getContent().forEach(d -> dos.add(toDTO(d)));

        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.getTotalElements(), pages.getSize()));
        return baseResponse;
    }

    @Override
    public SubjectCode create(SubjectCode subjectCode) {
        return subjectCodeRepository.save(subjectCode);
    }

    @Override
    public BaseResponse save(SubjectCodeRequestDTO subjectCodeRequest) {
        Optional<Section> sectionOptional = sectionService.findById(subjectCodeRequest.getSection());
        if(sectionOptional.isEmpty()){
            return error("Section is required");
        }

        Optional<SchoolYear> schoolYear = schoolYearRepository.findByIsCurrent(true);
        if(schoolYear.isEmpty()){
            return error("School year not yet started");
        }
        List<SubjectCode> subjectCodes = new ArrayList<>();
        for (SubjectCodeDTO dto : subjectCodeRequest.getSubjects()){
            // Check DB if the subjectCode.code already exist

//            SubjectCode subjectCode = subjectCodeRepository.findByCode(dto.getCode()).orElse(new SubjectCode());


            // Check subjectCode if exist from the DB and return an error if does not exist
            Optional<Subject> subject = subjectService.findByCode(dto.getSubjectCode());
            if(subject.isEmpty()){
                return error("Subjects is required");
            }

            Optional<SubjectCode> subjectCodeTmp = subjectCodeRepository.findBySectionAndSubjectAndSchoolYear(sectionOptional.get(), subject.get(), schoolYear.get().getYear());
            SubjectCode subjectCode;
            if(subjectCodeTmp.isPresent()){
//                return error("Subject code for Grade level [" + sectionOptional.get().getName() +"] already exist.");
                subjectCode = subjectCodeTmp.get();
            }else{
                subjectCode = new SubjectCode();
                subjectCode.setEndTime(dto.getEndTime());
                subjectCode.setStartTime(dto.getStartTime());
            }

            subjectCode.setSubject(subject.get());
            // Set value if adding new subject code
            if(dto.getId() == 0) {
                subjectCode.setSchoolYear(schoolYear.get().getYear());
                subjectCode.setCode(CodeGenerator.generateCode());
            }
            // Set weekdays as default class days
            if(subjectCode.getDays() == null || subjectCode.getDays().isEmpty()){
                subjectCode.setDays(Days.WEEKDAYS);
            }



            if(dto.getRoom() != null && !dto.getRoom().isBlank() && StringValidator.isNumeric(dto.getRoom())){
                Optional<Room> roomOption = roomService.findById(Long.parseLong(dto.getRoom()));
                subjectCode.setRoom(roomOption.orElse(null));
            }

            if(dto.getAdviser() != null && !dto.getAdviser().isBlank() && StringValidator.isNumeric(dto.getAdviser())){
                Optional<Teacher> teacherOptional = teacherService.findById(Long.parseLong(dto.getAdviser()));
                subjectCode.setAdviser(teacherOptional.orElse(null));
            }else{
                subjectCode.setAdviser(null);
            }

            subjectCode.setSection(sectionOptional.get());

            subjectCodes.add(subjectCode);
        }
        subjectCodeRepository.saveAll(subjectCodes);
        return new BaseResponse().success();
    }

    @Override
    public BaseResponse getSubjectsByAdviser(Long adviserId, long schoolYear) {
        Optional<Teacher> teacher = teacherService.findById(adviserId);
        if(teacher.isEmpty()){
            return error("Invalid adviser request");
        }
        List<SubjectCode> subjectCodes = subjectCodeRepository.findAllByAdviserAndSchoolYear(teacher.get(), schoolYear);
        List<SubjectCodeDTO> dos = new ArrayList<>();
        subjectCodes.forEach(d -> dos.add(toDTO(d)));
        BaseResponse baseResponse = success("Success").build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(subjectCodes.size(), 0));
        return baseResponse;
    }

    @Override
    public BaseResponse getStudentsBySubjectCode(String code){
        Optional<SubjectCode> subjectCode = subjectCodeRepository.findByCode(code);
        if(subjectCode.isEmpty()){
            return error("Invalid subject code request.");
        }

        List<Enrollment> enrollments = enrollmentService.findAllBySectionAndSubject(subjectCode.get().getSection());
        List<EnrollmentDTO> dos = new ArrayList<>();
        enrollments.forEach(d-> dos.add(enrollmentService.toDTO(d)));
        BaseResponse baseResponse = success("Success").build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(enrollments.size(), 0));
        return baseResponse;
    }

    @Override
    public Optional<SubjectCode> findByCode(String subjectCode) {
        return subjectCodeRepository.findByCode(subjectCode);
    }

    private SubjectCodeDTO toDTO(SubjectCode d){
        SubjectCodeDTO dto = new SubjectCodeDTO();
        dto.setId(d.getId());
        dto.setCode(d.getCode());
        if(d.getAdviser() != null) {
            dto.setAdviser(d.getAdviser().getFullName());
        }
        dto.setSubjectCode(d.getSubject().getCode());
        dto.setSubjectName(d.getSubject().getName());
        dto.setSchoolYear(d.getSchoolYear());
        dto.setStartTime(d.getStartTime());
        dto.setEndTime(d.getEndTime());
        dto.setYearLevel(d.getSubject().getYearLevel().getName());
        dto.setSection(d.getSection().getCode());
        if(d.getRoom() != null) {
            dto.setRoom(d.getRoom().getBuilding() + " - " + d.getRoom().getRoomNo());
        }
        return dto;
    }
}
