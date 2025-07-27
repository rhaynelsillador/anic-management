package com.sillador.strecs.admin.service;

import com.sillador.strecs.admin.dto.RoleDTO;
import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

public interface RoleService {

    BaseResponse getAll(Map<String, String> query);

    Optional<Role> findById(long id);
    Optional<Role> findByName(String name);
    BaseResponse getById(long id);

    BaseResponse updateRole(long id, @Valid RoleDTO roleDTO);

    BaseResponse newRole(@Valid RoleDTO roleDTO);
}
