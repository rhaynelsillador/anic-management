package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.*;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubjectCodeSpecification{

    private SubjectCodeSpecification(){
        super();
    }

    private static final List<String> filterKeys = List.of("code", "subjectCode", "yearLevel", "section", "adviser", "schoolYear", "room", "startTime", "endTime", "subject.units");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<SubjectCode> sortBy(String sortBy, boolean direction) {
        return (root, query, cb) -> {


            Join<SubjectCode, Subject> subjectJoin = null;
            Join<Subject, YearLevel> yearLevelJoin = null;
            Join<SubjectCode, Section> sectionJoin = null;


            // Handle sorting
            if (sortBy != null && !sortBy.isBlank()) {
                Expression<?> sortExpression;
                switch (sortBy) {
                    case "yearLevel":
                        subjectJoin = root.join("subject");
                        yearLevelJoin = subjectJoin.join("yearLevel");
                        sortExpression = yearLevelJoin.get("name");
                        break;
                    case "section":
                        sectionJoin = root.join("section");
                        sortExpression = sectionJoin.get("code");
                        break;
                    default:
                        sortExpression = root.get(sortBy);
                }

                assert query != null;
                if ("desc".equalsIgnoreCase(String.valueOf(direction))) {
                    query.orderBy(cb.desc(sortExpression));
                } else {
                    query.orderBy(cb.asc(sortExpression));
                }
            }

            return cb.conjunction();

        };
    }

    public static Sort sorting(String sortParam, List<String> sortedKeys){
        // Handle sorting
        Sort sort = Sort.unsorted();
        if (sortParam != null) {
            String[] parts = sortParam.split(",");
            String sortField = parts[0];

            Sort.Direction direction;
            if(sortedKeys.contains(sortField)) {
                if(sortField.equals("yearLevel")){
                    sortField = "subject.yearLevel.name";
                }else if(sortField.equals("subjectCode")){
                    sortField = "subject.code";
                }
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


    public static Specification<SubjectCode> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    Join<SubjectCode, Subject> subject = root.join("subject");


                    predicates.add(criteriaBuilder.equal(subject.get("active").as(boolean.class), true));

                    switch (key) {

                        case "yearLevel":
                            Join<SubjectCode, Subject> subjectJoin = root.join("subject");
                            // Join Subject → YearLevel
                            Join<Subject, YearLevel> yearLevelJoin = subjectJoin.join("yearLevel");

                            predicates.add(criteriaBuilder.like(
                                    yearLevelJoin.get("name").as(String.class), value
                            ));
                            break;
                        case "section":
                            Join<SubjectCode, Section> section = root.join("section");
                            // Join Subject → YearLevel
                            predicates.add(criteriaBuilder.like(
                                    section.get("code").as(String.class), value
                            ));
                            break;
                        case "adviser":
                            Join<SubjectCode, Teacher> adviser = root.join("adviser");

                            Expression<String> fullName = criteriaBuilder.concat(
                                    criteriaBuilder.concat(adviser.get("firstName"), " "),
                                    adviser.get("lastName")
                            );

                            Predicate fullNameLike = criteriaBuilder.like(
                                    criteriaBuilder.lower(fullName),
                                    "%" + value.toLowerCase() + "%"
                            );
                            
                            predicates.add(fullNameLike);

                            break;
                        case "subject.units":
                        case "subjectCode":
                            if(key.equals("subjectCode")) {
                                predicates.add(
                                    criteriaBuilder.or(
                                        criteriaBuilder.like(
                                            criteriaBuilder.lower(subject.get("code").as(String.class)), 
                                            "%" + value.toLowerCase() + "%"
                                        ),
                                        criteriaBuilder.like(
                                            criteriaBuilder.lower(subject.get("name").as(String.class)), 
                                            "%" + value.toLowerCase() + "%"
                                        )
                                    )
                                );

                            }else{
                                predicates.add(criteriaBuilder.equal(
                                        subject.get("units").as(Integer.class), Integer.parseInt(value)
                                ));
                            }

                            break;
                        default:
                            Path<String> path = root.get(key);
                            predicates.add(criteriaBuilder.like(path.as(String.class), "%" + value + "%"));
                            break;
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
