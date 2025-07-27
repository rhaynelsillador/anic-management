package com.sillador.strecs.repositories.specifications;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseSpecification {

    BaseSpecification(){}

    public static Sort sorting(String sortParam, List<String> sortedKeys){
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
        return sort;
    }

}
