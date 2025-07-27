package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.Section;
import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.entity.YearLevel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SectionSpecification extends BaseSpecification{

    private SectionSpecification(){
        super();
    }

    private static final List<String> filterKeys = List.of("code", "name", "yearLevel", "adviser", "schoolYear");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<Section> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    Path<String> path = root.get(key);


                    if(key.equals("yearLevel")) {
                        Join<Section, YearLevel> yearLevelJoin = root.join("yearLevel"); // adjust if different field name
                        predicates.add(criteriaBuilder.like(
                                yearLevelJoin.get("name").as(String.class), "%" + value + "%"
                        ));
                        System.out.println("?????????");
                    }else{
                        predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
