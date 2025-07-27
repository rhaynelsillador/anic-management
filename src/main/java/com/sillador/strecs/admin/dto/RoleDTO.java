package com.sillador.strecs.admin.dto;

import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Permission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class RoleDTO {

    private Long id;

    @NotNull(message = "Role name is required")
    private String name; // e.g. ROLE_USER, ROLE_ADMIN

    private String description;

}
