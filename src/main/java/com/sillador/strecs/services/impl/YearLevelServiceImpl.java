package com.sillador.strecs.services.impl;

import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.repositories.YearLevelRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.SectionSpecification;
import com.sillador.strecs.repositories.specifications.YearLevelSpecification;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
public class YearLevelServiceImpl implements YearLevelService {

    private final YearLevelRepository yearLevelRepository;

    public YearLevelServiceImpl(YearLevelRepository yearLevelRepository){
        this.yearLevelRepository = yearLevelRepository;
    }


    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));

        Sort sort = BaseSpecification.sorting(sorting, SectionSpecification.getSortedKeys());
        Specification<YearLevel> spec = YearLevelSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<YearLevel> pages = yearLevelRepository.findAll(spec, pageable);

        BaseResponse baseResponse = new BaseResponse().build(pages.getContent());
        baseResponse.setPage(new com.sillador.strecs.utility.Page(pages.getTotalElements(), pages.getSize()));
        return baseResponse;
    }

    @Override
    public Optional<YearLevel> findById(Long id) {
        return yearLevelRepository.findById(id);
    }
}
