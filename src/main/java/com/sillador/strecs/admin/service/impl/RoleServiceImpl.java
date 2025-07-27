package com.sillador.strecs.admin.service.impl;

import com.sillador.strecs.admin.dto.AccountDTO;
import com.sillador.strecs.admin.dto.RoleDTO;
import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.admin.repository.AccountRepository;
import com.sillador.strecs.admin.repository.RoleRepository;
import com.sillador.strecs.admin.service.AccountService;
import com.sillador.strecs.admin.service.RoleService;
import com.sillador.strecs.services.impl.BaseService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends BaseService implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public BaseResponse getAll(Map<String, String> query) {

        List<Role> pages = roleRepository.findAll();
        List<RoleDTO> roleDTOS = new ArrayList<>();
        pages.forEach(d ->roleDTOS.add(toDTO(d)));

        return success().build(roleDTOS);
    }

    @Override
    public Optional<Role> findById(long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }


    @Override
    public BaseResponse getById(long id) {
        Optional<Role> role = roleRepository.findById(id);
        if(role.isEmpty()){
            return error("Role does not exist");
        }
        return success().build(toDTO(role.get()));
    }

    @Override
    public BaseResponse updateRole(long id, RoleDTO roleDTO) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if(roleOptional.isEmpty()){
            return error("Role does not exist");
        }
        Optional<Role> roleOptional1 = roleRepository.findByName(roleDTO.getName());
        if(roleOptional1.isPresent() && !Objects.equals(roleOptional1.get().getId(), roleOptional.get().getId())){
            return error("Role name already exist");
        }

        Role role = roleOptional.get();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());

        roleRepository.save(role);

        return success();
    }

    @Override
    public BaseResponse newRole(RoleDTO roleDTO) {
        Optional<Role> roleOptional = roleRepository.findByName(roleDTO.getName());
        if(roleOptional.isPresent()){
            return error("Role name already exist");
        }

        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());


        roleRepository.save(role);
        return success();
    }

    private RoleDTO toDTO(Role d){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(d.getId());
        roleDTO.setDescription(d.getDescription());
        roleDTO.setName(d.getName());
        return roleDTO;
    }
}