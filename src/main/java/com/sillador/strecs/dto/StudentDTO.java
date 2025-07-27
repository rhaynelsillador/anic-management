package com.sillador.strecs.dto;

import com.sillador.strecs.entity.StudentInformation;
import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class StudentDTO {
    private long id;
    private String lrn;
    private String studentId;
    @NotNull(message = "Firstname is required")
    private String firstName;
    @NotNull(message = "Lastname is required")
    private String lastName;
    @NotNull(message = "Middle name is required")
    private String middleName;
    private String fullName;
    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotNull(message = "Birthday is required")
    private Date birthday;
    private String address;
    private String contactNumber;
    private String email;
    private String photoUrl;
    private StudentStatus status;
//    private java.util.Date graduated;
//    private Integer batch;

    private StudentInformationDTO information;

}
