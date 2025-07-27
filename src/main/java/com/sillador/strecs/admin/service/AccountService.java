package com.sillador.strecs.admin.service;

import com.sillador.strecs.admin.dto.AccountDTO;
import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.utility.BaseResponse;

import java.util.Map;
import java.util.Optional;

public interface AccountService {

    BaseResponse getAll(Map<String, String> query);

    Optional<Account> findByIdentifier(String identifier);

    BaseResponse findBySmartIdentifier(Object identifier);

    BaseResponse findById(long id);

    BaseResponse create(AccountDTO accountDTO);

    BaseResponse update(long id, AccountDTO accountDTO);
}
