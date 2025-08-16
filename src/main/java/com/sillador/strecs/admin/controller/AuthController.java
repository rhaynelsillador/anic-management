package com.sillador.strecs.admin.controller;

import com.sillador.strecs.admin.dto.LoginRequest;
import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.admin.service.impl.AccountServiceImpl;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountServiceImpl accountService;
    private final TeacherService teacherService;

    public AuthController(AccountServiceImpl accountService, TeacherService teacherService) {
        this.accountService = accountService;
        this.teacherService = teacherService;
    }

    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Account> optionalUser = accountService.findByIdentifier(loginRequest.getIdentifier());

        if (optionalUser.isEmpty()) {
            return new BaseResponse().build(ResponseCode.ERROR, "Invalid username/email");
        }

        Account account = optionalUser.get();

        if (!accountService.validatePassword(account, loginRequest.getPassword())) {
            return new BaseResponse().build(ResponseCode.ERROR, "Invalid password");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", account.getId());
        response.put("identifier", account.getUsername());
        response.put("accountRef", account.getAccountRef());
        response.put("roles", account.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return new BaseResponse().build(response).success();
    }

    @GetMapping("/profile/{accountId}")
    public BaseResponse getProfile(@PathVariable Long accountId) {
        BaseResponse accountResponse = accountService.findById(accountId);
        
        if (!accountResponse.getStatus().equals("SUCCESS")) {
            return new BaseResponse().build(ResponseCode.ERROR, "Account not found");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> accountData = (Map<String, Object>) accountResponse.getData();
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", accountData.get("id"));
        profile.put("username", accountData.get("username"));
        profile.put("email", accountData.get("email"));
        profile.put("fullName", accountData.get("fullName"));
        profile.put("accountRef", accountData.get("accountRef"));
        profile.put("enabled", accountData.get("enabled"));
        profile.put("createdAt", accountData.get("createdAt"));
        profile.put("updatedAt", accountData.get("updatedAt"));
        profile.put("roles", accountData.get("roles"));
        
        // Check if this account has teacher information
        Object accountRefObj = accountData.get("accountRef");
        if (accountRefObj != null) {
            Long accountRef = ((Number) accountRefObj).longValue();
            Optional<Teacher> teacherOpt = teacherService.findByAccountRef(accountRef);
            
            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                Map<String, Object> teacherInfo = new HashMap<>();
                teacherInfo.put("id", teacher.getId());
                teacherInfo.put("employeeNo", teacher.getEmployeeNo());
                teacherInfo.put("firstName", teacher.getFirstName());
                teacherInfo.put("lastName", teacher.getLastName());
                teacherInfo.put("fullName", teacher.getFullName());
                teacherInfo.put("email", teacher.getEmail());
                teacherInfo.put("contactNo", teacher.getContactNo());
                teacherInfo.put("photoUrl", teacher.getPhotoUrl());
                teacherInfo.put("position", teacher.getPosition());
                teacherInfo.put("createdDate", teacher.getCreatedDate());
                teacherInfo.put("updatedDate", teacher.getUpdatedDate());
                
                profile.put("teacherInfo", teacherInfo);
            }
        }
        
        return new BaseResponse().build(profile);
    }
}
