package com.sillador.strecs.admin.controller;

import com.sillador.strecs.admin.dto.LoginRequest;
import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.admin.service.impl.AccountServiceImpl;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountServiceImpl accountService;

    public AuthController(AccountServiceImpl accountService) {
        this.accountService = accountService;
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
}
