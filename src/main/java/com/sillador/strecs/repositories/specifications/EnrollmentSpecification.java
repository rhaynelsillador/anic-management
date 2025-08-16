package com.sillador.strecs.repositories.specifications;

import com.sillador.strecs.entity.*;
import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnrollmentSpecification{

    private EnrollmentSpecification(){
        super();
    }

    private static final List<String> filterKeys = List.of("student.studentId", "student.lrn", "student.firstName", "student.lastName","enrollmentDate","yearLevel", "section.adviser.firstName", "schoolYear", "section", "student.gender", "student.contactNumber", "status");
    @Getter
    private static final List<String> sortedKeys = filterKeys;

    public static Specification<Enrollment> filter(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (value != null && !value.isBlank() && filterKeys.contains(key)) {
                    Join<Enrollment, Student> studentJoin = root.join("student");
                    Join<SubjectCode, Section> section;

                    switch (key) {
                        case "student.studentId":
                            predicates.add(criteriaBuilder.like(
                                    studentJoin.get("studentId").as(String.class), "%"+value+"%"
                            ));
                            break;
                        case "student.firstName":
                            predicates.add(criteriaBuilder.like(
                                    studentJoin.get("firstName").as(String.class), "%"+value+"%"
                            ));
                            break;
                        case "student.lastName":

                            predicates.add(criteriaBuilder.like(
                                    studentJoin.get("lastName").as(String.class), "%"+value+"%"
                            ));
                            break;
                        case "student.lrn":

                            predicates.add(criteriaBuilder.like(
                                    studentJoin.get("lrn").as(String.class), "%"+value+"%"
                            ));
                            break;
                        case "student.status":
                            Integer status = null;
                            try {
                                status = StudentStatus.valueOf(value.toUpperCase()).ordinal();
                            } catch (Exception ignored) {
                                ignored.printStackTrace();
                            }
                            predicates.add(criteriaBuilder.equal(
                                    studentJoin.get("status").as(Integer.class), status
                            ));
                            break;
                        case "student.gender":
                            Integer gender = null;
                            try {
                                gender = Gender.valueOf(value.toUpperCase()).ordinal();
                            } catch (Exception ignored) {
                            }
                            predicates.add(criteriaBuilder.equal(
                                    studentJoin.get("gender").as(Integer.class), gender
                            ));
                            break;
                        case "section.adviser.firstName":
                            section = root.join("section");
                            Join<Section, Teacher> adviser = section.join("adviser");
                            // Join Subject → YearLevel
                            predicates.add(criteriaBuilder.like(
                                    adviser.get("firstName").as(String.class), "%"+value+"%"
                            ));
                            break;
                        case "student.contactNumber":

                            // Join Subject → YearLevel
                            predicates.add(criteriaBuilder.like(
                                    studentJoin.get("contactNumber").as(String.class), "%"+value+"%"
                            ));
                            break;

                        case "section":
                            section = root.join("section");
                            // Join Subject → YearLevel
                            predicates.add(criteriaBuilder.like(
                                    section.get("code").as(String.class), "%"+value.toUpperCase()+"%"
                            ));
                            break;
                        case "status":
                            StudentStatus studentStatus;
                            try {
                                studentStatus = StudentStatus.valueOf(value.toUpperCase());
                                Path<String> path = root.get(key);
                                predicates.add(criteriaBuilder.equal(path.as(Integer.class), studentStatus.ordinal()));
                            } catch (Exception ignored) {
                                Path<String> path = root.get(key);
                                predicates.add(criteriaBuilder.equal(path.as(Integer.class), 100));
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
