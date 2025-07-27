package com.sillador.strecs.admin.controller;

import com.sillador.strecs.admin.dto.RoleDTO;
import com.sillador.strecs.admin.service.AccountService;
import com.sillador.strecs.admin.service.RoleService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/role")
public class AdminRoleRestController {
    private final RoleService roleService;

    public AdminRoleRestController(RoleService roleService){
        this.roleService = roleService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query) throws ParseException {
        return roleService.getAll(query);
    }

    @PostMapping
    public BaseResponse newRole(@Valid  @RequestBody RoleDTO roleDTO){
        return roleService.newRole(roleDTO);
    }

    @PutMapping("/{id}")
    public BaseResponse updateRole(@PathVariable long id, @Valid  @RequestBody RoleDTO roleDTO){
        return roleService.updateRole(id, roleDTO);
    }


}
