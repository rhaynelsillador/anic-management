package com.sillador.strecs.admin.service.impl;

import com.sillador.strecs.admin.dto.AccountDTO;
import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.admin.repository.AccountRepository;
import com.sillador.strecs.admin.service.AccountService;
import com.sillador.strecs.admin.service.RoleService;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.services.impl.BaseService;
import com.sillador.strecs.utility.BaseResponse;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountServiceImpl extends BaseService implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final TeacherService teacherService;

    private static final String ACCOUNT_DOES_NOT_EXIST_MSG = "Account does not exist";

    @Override
    public BaseResponse getAll(Map<String, String> query) {

        List<Account> pages = accountRepository.findAll();
        List<AccountDTO> accountDTOS = new ArrayList<>();
        pages.forEach(d ->accountDTOS.add(toDTO(d)));

        return success().build(accountDTOS);
    }

    @Override
    public Optional<Account> findByIdentifier(String identifier) {
        return accountRepository.findByUsername(identifier)
                .or(() -> accountRepository.findByEmail(identifier));
    }

    @Override
    public BaseResponse findBySmartIdentifier(Object identifier) {
        return switch (identifier) {
            case Integer i -> findById(i.longValue());
            case Long l    -> findById(l);
            case String s  -> findByEmailOrUsername(s);
            default        -> throw new IllegalArgumentException("Unsupported type");
        };


    }

    private BaseResponse findByEmailOrUsername(String identifier) {
        Optional<Account> account = findByIdentifier(identifier);
        if(account.isEmpty()){
            return error(ACCOUNT_DOES_NOT_EXIST_MSG);
        }
        return success().build(toDTO(account.get()));
    }

    @Override
    public BaseResponse findById(long id) {
        Optional<Account> account = accountRepository.findById(id);
        if(account.isEmpty()){
            return error(ACCOUNT_DOES_NOT_EXIST_MSG);
        }
        return success().build(toDTO(account.get()));
    }

    @Override
    public BaseResponse create(AccountDTO accountDTO) {
        Optional<Account> accountOptional = accountRepository.findByUsername(accountDTO.getUsername());
        if(accountOptional.isPresent()){
            return error("Username already exist");
        }

        if(accountDTO.getEmail() != null && !accountDTO.getEmail().isBlank()){
            accountOptional = accountRepository.findByEmail(accountDTO.getEmail());
            if(accountOptional.isPresent()){
                return error("Email already exist");
            }
        }

        if(accountDTO.getRoles() == null || accountDTO.getRoles().isEmpty()){
            return error("At least 1 role must be added");
        }

        Set<Role> roleSet = new HashSet<>();
        for(String role : accountDTO.getRoles()) {
            Optional<Role> roleOptional = roleService.findByName(role);
            if (roleOptional.isEmpty()) {
                return error("Role does not exist");
            }

            roleSet.add(roleOptional.get());
        }

        Account account = new Account();
        account.setEnabled(true);
        account.setUsername(accountDTO.getUsername());
        account.setEmail(accountDTO.getEmail());
        account.setRoles(roleSet);
        account.setPassword(hashPassword(accountDTO.getPassword()));

        account.setAccountRef(accountDTO.getAccountRef());

        accountRepository.save(account);
        return success();
    }

    @Override
    public BaseResponse update(long id, AccountDTO accountDTO) {
        Optional<Account> account = accountRepository.findById(id);
        if(account.isEmpty()){
            return error(ACCOUNT_DOES_NOT_EXIST_MSG);
        }

        if(accountDTO.getRoles() == null || accountDTO.getRoles().isEmpty()){
            return error("At least 1 role must be added");
        }

        Set<Role> roleSet = new HashSet<>();
        for(String role : accountDTO.getRoles()) {
            Optional<Role> roleOptional = roleService.findByName(role);
            if (roleOptional.isEmpty()) {
                return error("Role does not exist");
            }

            roleSet.add(roleOptional.get());
        }

        Account account1 = account.get();
        account1.setEmail(accountDTO.getEmail());

        account1.setRoles(roleSet);

        account1.setAccountRef(accountDTO.getAccountRef());

        accountRepository.save(account1);
        return success();
    }

    public boolean validatePassword(Account account, String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, account.getPassword());
    }

    public String hashPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private AccountDTO toDTO(Account d){
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(d.getId());
        accountDTO.setEnabled(d.isEnabled());
        accountDTO.setUsername(d.getUsername());
        accountDTO.setUpdatedAt(d.getUpdatedAt());
        accountDTO.setCreatedAt(d.getCreatedAt());
        accountDTO.setEmail(d.getEmail());
        accountDTO.setAccountRef(d.getAccountRef());
        accountDTO.setRoles(d.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return accountDTO;
    }

    @Override
    public BaseResponse getCounselorsList() {
        List<AccountDTO> counselors = new ArrayList<>();
        accountRepository.findAllByRolesName("Counselor").forEach(d -> {
            AccountDTO accountDTO = toDTO(d);
            if(d.getAccountRef() != null) {
                Optional<Teacher> optional = teacherService.findById(d.getAccountRef());
                if(optional.isPresent()){
                    accountDTO.setTeacher(teacherService.toDTO(optional.get()));
                } else {
                    accountDTO.setTeacher(null);
                }

            }

            counselors.add(accountDTO);
        });
        
        return success().build(counselors);
    }
}