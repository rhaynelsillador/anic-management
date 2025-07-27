package com.sillador.strecs.dto;

import com.sillador.strecs.enums.Gender;
import com.sillador.strecs.enums.StudentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class TeacherDTO {
    private long id;
    @NotNull(message = "Employee No is required")
    private String employeeNo;
    @NotNull(message = "Firstname is required")
    private String firstName;
    @NotNull(message = "Lastname is required")
    private String lastName;
    private String email;
    private String contactNo;
    private String photoUrl;
    @NotNull(message = "Position is required")
    private String position;

    private String fullName;
}
