package com.sillador.strecs.admin.controller;

import com.sillador.strecs.admin.dto.AccountDTO;
import com.sillador.strecs.admin.service.AccountService;
import com.sillador.strecs.utility.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/account")
public class AdminAccountRestController {
    private final AccountService accountService;

    public AdminAccountRestController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query) throws ParseException {
        return accountService.getAll(query);
    }

    @PostMapping
    public BaseResponse saveNewAccount(@Valid @RequestBody AccountDTO accountDTO) throws ParseException {
        return accountService.create(accountDTO);
    }


    @PutMapping("/{id}")
    public BaseResponse saveNewAccount(@PathVariable long id, @Valid @RequestBody AccountDTO accountDTO) throws ParseException {
        return accountService.update(id, accountDTO);
    }
}
