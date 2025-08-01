package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.Document;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentSpecification {
    private DocumentSpecification() {
        super();
    }

    private static final List<String> filterKeys = List.of("title", "category", "content", "publish", "draft", "createdAt", "updatedAt");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<Document> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    var path = root.get(key);
                    if (key.equals("publish") || key.equals("draft")) {
                        predicates.add(criteriaBuilder.equal(path.as(Boolean.class), Boolean.parseBoolean(value)));
                    } else {
                        predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
