package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.Student;
import com.sillador.strecs.entity.Teacher;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeacherSpecification extends BaseSpecification{

    private TeacherSpecification(){
        super();
    }

    private static final List<String> filterKeys = List.of("firstName", "lastName", "middleName", "employeeNo", "position", "contactNo", "status");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<Teacher> filter(Map<String, String> params) {
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
