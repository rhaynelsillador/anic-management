package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.Section;
import com.sillador.strecs.entity.YearLevel;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YearLevelSpecification extends BaseSpecification{

    private YearLevelSpecification(){
        super();
    }

    private static final List<String> filterKeys = List.of("name");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<YearLevel> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    Path<String> path = root.get(key);
                    predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
