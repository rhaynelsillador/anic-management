package com.sillador.strecs.admin.controller;

import com.sillador.strecs.admin.dto.AccountDTO;
import com.sillador.strecs.admin.service.impl.AccountServiceImpl;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountServiceImpl accountService;
    private final TeacherService teacherService;

    public AccountController(AccountServiceImpl accountService, TeacherService teacherService) {
        this.accountService = accountService;
        this.teacherService = teacherService;
    }

    @GetMapping("/profile/{accountId}")
    public BaseResponse getProfile(@PathVariable Long accountId) {
        BaseResponse accountResponse = accountService.findById(accountId);
        
        if (!accountResponse.getStatus().equals("SUCCESS")) {
            return new BaseResponse().build(ResponseCode.ERROR, "Account not found");
        }

        AccountDTO accountData = (AccountDTO) accountResponse.getData();
        Map<String, Object> profile = new HashMap<>();
        profile.put("account", accountData);
        if (accountData != null && accountData.getAccountRef() != null) {
            Long accountRef = accountData.getAccountRef();
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
