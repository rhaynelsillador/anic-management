package com.sillador.strecs.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

import com.sillador.strecs.dto.TeacherDTO;

@Setter
@Getter
public class AccountDTO {

    private Long id;
    @NotNull(message = "Username is required")
    @Size(min = 3, max = 32, message = "User must be 7 - 32 characters")
    private String username;
    private String email;

//    @NotNull(message = "Password is required")
//    @Size(min = 7, max = 32, message = "Password must be 7 - 32 characters")
    private String password;

    private boolean enabled ;
    // Optional metadata
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotEmpty(message = "At least 1 role must be added")
    private Set<String> roles;

    private Long roleId;

    private Long accountRef;

    private TeacherDTO teacher;
}
