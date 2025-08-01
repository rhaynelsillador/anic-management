package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.Section;
import com.sillador.strecs.entity.Subject;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.entity.YearLevel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubjectSpecification extends BaseSpecification{

    private SubjectSpecification(){
        super();
    }

    private static final List<String> filterKeys = List.of("code", "active", "name", "units", "active", "yearLevel", "createdDate", "updatedDate");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<Subject> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    Path<String> path = root.get(key);
                    if(key.equals("yearLevel")) {
                        Join<Section, YearLevel> yearLevelJoin = root.join("yearLevel"); // adjust if different field name
                        predicates.add(criteriaBuilder.like(
                                yearLevelJoin.get("name").as(String.class), value
                        ));
                    }else if(key.equals("active")){
                        predicates.add(criteriaBuilder.equal(path.as(Boolean.class),  Boolean.parseBoolean(value)));
                    }else{
                        predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
