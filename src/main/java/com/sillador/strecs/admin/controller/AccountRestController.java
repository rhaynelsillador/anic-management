package com.sillador.strecs.admin.controller;

import com.sillador.strecs.admin.service.AccountService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/account")
public class AccountRestController {
    private final AccountService accountService;

    public AccountRestController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping("/{identifier}")
    public BaseResponse findBySmartIdentifier(@PathVariable Object identifier) {
        return accountService.findBySmartIdentifier(identifier);
    }

    @GetMapping("/counselors")
    public BaseResponse findCounselorsList() {
        return accountService.getCounselorsList();
    }
}
