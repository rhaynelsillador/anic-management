package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.YearLevelDTO;
import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.repositories.YearLevelRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.SectionSpecification;
import com.sillador.strecs.repositories.specifications.YearLevelSpecification;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.CodeGenerator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YearLevelServiceImpl extends BaseService implements YearLevelService {

    private final Logger logger = LoggerFactory.getLogger(YearLevelServiceImpl.class);
    private final YearLevelRepository yearLevelRepository;

    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {
//        String sorting = query.getOrDefault("sort", "id,desc");
//        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
//        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));
//
//        Sort sort = BaseSpecification.sorting(sorting, SectionSpecification.getSortedKeys());
//        Specification<YearLevel> spec = YearLevelSpecification.filter(query);
//
//        Pageable pageable = PageRequest.of(page, limit, sort);

        List<YearLevel> pages = yearLevelRepository.findAll();

        List<YearLevelDTO> dos = new ArrayList<>();
        pages.forEach(d -> dos.add(toDTO(d)));
        BaseResponse baseResponse = new BaseResponse().build(dos);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.size(), 0));
        return baseResponse;
    }

    @Override
    public Optional<YearLevel> findById(Long id) {
        return yearLevelRepository.findById(id);
    }

    @Override
    public BaseResponse updateClass(long id, YearLevelDTO yearLevelDTO) {
        Optional<YearLevel> yearLevelOptional = yearLevelRepository.findById(id);
        if(yearLevelOptional.isEmpty()){
            return error("Class does not exist");
        }
        YearLevel yearLevel = toYearLevel(yearLevelDTO, yearLevelOptional.get());
        if(yearLevelDTO.getPrerequisiteYear() != null) {
            yearLevelOptional = yearLevelRepository.findById(yearLevelDTO.getPrerequisiteYear().getId());
            if(yearLevelOptional.isEmpty()) {
                return error("Prerequisite class does not exist");
            }
            yearLevel.setPrerequisiteYear(yearLevelOptional.get());
        }

        if(yearLevel.getCode() == null){
            yearLevel.setCode(CodeGenerator.generateCode());
        }
        logger.info("Class updated : {} to {}", yearLevel.getClass(), yearLevel.getName());
        yearLevelRepository.save(yearLevel);
        return success();
    }

    @Override
    public BaseResponse createNewClass(YearLevelDTO yearLevelDTO) {
        YearLevel yearLevel = toYearLevel(yearLevelDTO, null);
        if(yearLevelDTO.getPrerequisiteYear() != null) {
            Optional<YearLevel> yearLevelOptional = yearLevelRepository.findById(yearLevelDTO.getPrerequisiteYear().getId());
            if(yearLevelOptional.isEmpty()) {
                return error("Prerequisite class does not exist");
            }

            yearLevel.setPrerequisiteYear(yearLevelOptional.get());
        }

        if(yearLevel.getCode() == null){
            yearLevel.setCode(CodeGenerator.generateCode());
        }

        logger.info("Class added : {} to {}", yearLevel.getClass(), yearLevel.getName());
        yearLevelRepository.save(yearLevel);
        return success();
    }

    private YearLevel toYearLevel(YearLevelDTO yearLevelDTO, @Nullable YearLevel yearLevel){
        if(yearLevel == null){
            yearLevel = new YearLevel();
        }

        yearLevel.setGroupYearLevel(yearLevelDTO.getGroupYearLevel());
        yearLevel.setLastLevel(yearLevelDTO.isLastLevel());
        yearLevel.setName(yearLevelDTO.getName());
        yearLevel.setCode(yearLevelDTO.getCode());

        return yearLevel;
    }

    private YearLevelDTO toDTO(YearLevel yearLevel){
        YearLevelDTO yearLevelDTO = new YearLevelDTO();
        yearLevelDTO.setId(yearLevel.getId());
        yearLevelDTO.setCode(yearLevel.getCode());
        yearLevelDTO.setName(yearLevel.getName());
        yearLevelDTO.setGroupYearLevel(yearLevel.getGroupYearLevel());
        if(yearLevel.getPrerequisiteYear() != null){
            YearLevelDTO prereq = new YearLevelDTO();
            YearLevel pre = yearLevel.getPrerequisiteYear();
            prereq.setId(pre.getId());
            prereq.setCode(pre.getCode());
            prereq.setName(pre.getName());
            yearLevelDTO.setPrerequisiteYear(prereq);
        }

        yearLevelDTO.setCreatedDate(yearLevel.getCreatedDate());
        yearLevelDTO.setUpdatedDate(yearLevel.getUpdatedDate());
        return yearLevelDTO;
    }
}
