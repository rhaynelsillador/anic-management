package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.SubjectDTO;
import com.sillador.strecs.dto.TeacherDTO;
import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.repositories.SubjectRepository;
import com.sillador.strecs.repositories.TeacherRepository;
import com.sillador.strecs.repositories.YearLevelRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.SubjectSpecification;
import com.sillador.strecs.repositories.specifications.TeacherSpecification;
import com.sillador.strecs.services.SubjectService;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SubjectServiceImpl extends BaseService implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final YearLevelRepository yearLevelRepository;
    public SubjectServiceImpl(SubjectRepository subjectRepository, YearLevelRepository yearLevelRepository){
        this.subjectRepository = subjectRepository;
        this.yearLevelRepository = yearLevelRepository;
    }


    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));

        Sort sort = BaseSpecification.sorting(sorting, SubjectSpecification.getSortedKeys());
        Specification<Subject> spec = SubjectSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Subject> pages = subjectRepository.findAll(spec, pageable);

        List<SubjectDTO> dos = new ArrayList<>();
        pages.getContent().forEach(d -> dos.add(toDTO(d)));

        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.getTotalElements(), pages.getSize()));
        return baseResponse;
    }


    @Override
    public Optional<Subject> findByCode(String subjectCode) {
        return subjectRepository.findByCode(subjectCode);
    }

    @Override
    public BaseResponse createSubject(@Valid  SubjectDTO subjectDTO) {
        Subject subject = toSubject(subjectDTO, null);
        Optional<YearLevel> yearLevel = yearLevelRepository.findByName(subjectDTO.getYearLevel());
        if(yearLevel.isEmpty()){
            return error("Year level does not exist");
        }
        subject.setYearLevel(yearLevel.get());
        subjectRepository.save(subject);
        return success();
    }

    @Override
    public BaseResponse updateSubject(long id, @Valid  SubjectDTO subjectDTO) {
        Optional<Subject> subjectOptional = subjectRepository.findById(id);
        if(subjectOptional.isEmpty()){
            return error("Subject resource does not exist");
        }
        Subject subject = toSubject(subjectDTO, subjectOptional.get());
        Optional<YearLevel> yearLevel = yearLevelRepository.findByName(subjectDTO.getYearLevel());
        if(yearLevel.isEmpty()){
            return error("Year level does not exist");
        }
        subject.setYearLevel(yearLevel.get());
        subjectRepository.save(subject);
        return success();
    }


    private Subject toSubject(SubjectDTO subjectDTO, Subject subject){
        if(subject == null) {
            subject = new Subject();
        }
        subject.setId(subjectDTO.getId());
        subject.setCode(subjectDTO.getCode());
        subject.setName(subjectDTO.getName());
        subject.setUnits(subjectDTO.getUnits());
        subject.setActive(subjectDTO.isActive());
        return  subject;
    }

    private SubjectDTO toDTO(Subject subject){
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setCode(subject.getCode());
        dto.setName(subject.getName());
        dto.setActive(subject.isActive());
        dto.setUnits(subject.getUnits());
        if(subject.getYearLevel() != null) {
            dto.setYearLevel(subject.getYearLevel().getName());
        }
        dto.setCreatedDate(subject.getCreatedDate());
        dto.setUpdatedDate(subject.getUpdatedDate());
        return  dto;
    }

}
