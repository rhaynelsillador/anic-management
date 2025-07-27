package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.SectionDTO;
import com.sillador.strecs.dto.SubjectDTO;
import com.sillador.strecs.entity.*;
import com.sillador.strecs.repositories.SchoolYearRepository;
import com.sillador.strecs.repositories.SectionRepository;
import com.sillador.strecs.repositories.SubjectRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.SectionSpecification;
import com.sillador.strecs.repositories.specifications.SubjectSpecification;
import com.sillador.strecs.repositories.specifications.TeacherSpecification;
import com.sillador.strecs.services.SectionService;
import com.sillador.strecs.services.SubjectService;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SectionServiceImpl extends BaseService implements SectionService {

    private final SectionRepository sectionRepository;
    private final TeacherService teacherService;
    private final YearLevelService yearLevelService;

    @Autowired
    private SchoolYearRepository schoolYearRepository;

    public SectionServiceImpl(SectionRepository sectionRepository, TeacherService teacherService, YearLevelService yearLevelService){
        this.sectionRepository = sectionRepository;
        this.teacherService = teacherService;
        this.yearLevelService = yearLevelService;
    }


    @Override
    public BaseResponse getAll(@NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));

        Sort sort = BaseSpecification.sorting(sorting, SectionSpecification.getSortedKeys());
        Specification<Section> spec = SectionSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Section> pages = sectionRepository.findAll(spec, pageable);

        List<SectionDTO> dtos = new ArrayList<>();
        pages.getContent().forEach(d -> {
            SectionDTO dto = new SectionDTO();
            dto.setId(d.getId());
            dto.setName(d.getName());
            dto.setCode(d.getCode());
            dto.setSchoolYear(d.getSchoolYear());
            if(d.getAdviser() != null){
                dto.setAdviser(d.getAdviser().getFullName());
                dto.setAdviserId(d.getAdviser().getId());
            }
            if(d.getYearLevel() != null) {
                dto.setYearLevel(d.getYearLevel().getName());
                dto.setYearLevelId(d.getYearLevel().getId());
            }
            dtos.add(dto);
        });

        BaseResponse baseResponse = new BaseResponse().build(dtos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.getTotalElements(), pages.getSize()));
        return baseResponse;
    }

    @Override
    public Optional<Section> findById(Long id) {
        return sectionRepository.findById(id);
    }

    @Override
    public BaseResponse update(Long id, SectionDTO dto) {
        Teacher teacher = null;
        if(dto.getAdviserId() != null) {
            Optional<Teacher> teacherOption = teacherService.findById(dto.getAdviserId());
            if (teacherOption.isEmpty()) {
                return error("Adviser request is invalid");
            }
            teacher = teacherOption.get();
        }

        Optional<YearLevel> yearLevel = yearLevelService.findById(dto.getYearLevelId());
        if(yearLevel.isEmpty()){
            return error("Grade level request is invalid");
        }

        Optional<Section> sectionOptional = sectionRepository.findById(id);
        if(sectionOptional.isEmpty()){
            return error("Section does not exist");
        }

        Section section = toSection(dto, sectionOptional.get());
        section.setAdviser(teacher);
        section.setYearLevel(yearLevel.get());
        section = sectionRepository.save(section);
        return success("Successfully updated section").build(section);
    }

    @Override
    public BaseResponse create(SectionDTO dto) {
        Optional<Teacher> teacher = teacherService.findById(dto.getAdviserId());
        if(teacher.isEmpty()){
            return error("Adviser request is invalid");
        }

        Optional<YearLevel> yearLevel = yearLevelService.findById(dto.getYearLevelId());
        if(yearLevel.isEmpty()){
            return error("Grade level request is invalid");
        }

        Optional<SchoolYear> schoolYear = schoolYearRepository.findByIsCurrent(true);
        if(schoolYear.isEmpty()){
            return error("School year not yet started");
        }


        Section section = toSection(dto, new Section());
        section.setSchoolYear(schoolYear.get().getYear());
        section.setAdviser(teacher.get());
        section.setYearLevel(yearLevel.get());
        section = sectionRepository.save(section);
        return success("Successfully created new section").build(section);
    }

    private Section toSection(SectionDTO dto, Section section){
        section.setCode(dto.getCode());
        section.setName(dto.getName());
        return section;
    }
}
