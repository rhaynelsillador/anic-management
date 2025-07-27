package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.Student;
import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentSpecification {

    private StudentSpecification(){

    }

    private static final List<String> filterKeys = List.of("studentId", "lrn", "firstName", "lastName", "middleName", "gender", "birthday", "contactNumber", "status", "information.graduated", "information.batch");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Sort sorting(String sortParam){
        // Handle sorting
        Sort sort = Sort.unsorted();
        if (sortParam != null) {
            String[] parts = sortParam.split(",");
            String sortField = parts[0];
            Sort.Direction direction;
            if(sortedKeys.contains(sortField)) {
                direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
            }else{
                return Sort.unsorted();
            }

            sort = Sort.by(direction, sortField);
        }
        System.out.println(sort);
        return sort;
    }
    public static Specification<Student> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    Path<String> path = root.get(key);
                    if (key.equals("gender")) {
                        Integer gender = null;
                        try {
                            gender = Gender.valueOf(value.toUpperCase()).ordinal();
                        } catch (Exception ignored) {
                        }
                        predicates.add(criteriaBuilder.equal(path.as(Integer.class), gender));
                    }else if (key.equals("status")) {
                        Integer status = null;
                        try {
                            status = StudentStatus.valueOf(value.toUpperCase()).ordinal();
                        } catch (Exception ignored) {
                        }
                        predicates.add(criteriaBuilder.equal(path.as(Integer.class), status));
                    } else{
                        predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
