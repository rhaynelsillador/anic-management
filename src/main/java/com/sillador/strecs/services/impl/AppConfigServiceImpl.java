package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.AppConfigDTO;
import com.sillador.strecs.entity.SchoolYear;
import com.sillador.strecs.entity.YearLevel;
import com.sillador.strecs.repositories.SchoolYearRepository;
import com.sillador.strecs.repositories.YearLevelRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.SectionSpecification;
import com.sillador.strecs.repositories.specifications.YearLevelSpecification;
import com.sillador.strecs.services.AppConfigService;
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
public class AppConfigServiceImpl extends BaseService implements AppConfigService {

    private final SchoolYearRepository schoolYearRepository;

    public AppConfigServiceImpl(SchoolYearRepository schoolYearRepository){
        this.schoolYearRepository = schoolYearRepository;
    }


    @Override
    public BaseResponse getAppConfig() {
        AppConfigDTO appConfig = new AppConfigDTO();
        Optional<SchoolYear> schoolYear = schoolYearRepository.findByIsCurrent(true);
        schoolYear.ifPresent(year -> appConfig.setSchoolYear(year.getYear()));
        return success().build(appConfig);
    }
}
