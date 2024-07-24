package com.vmo.management_fresher.service;

import java.util.List;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.response.AccountRes;
import com.vmo.management_fresher.model.Account;

public interface AccountService {
    String createAccount(String uid, AuthenticationReq request);

    String updateAccount(String uid, String id, AuthenticationReq request);

    String deleteAccount(String id);

    Account addRoleAccount(String uid, String id, String role);

    AccountRes getById(String uid, String id);

    List<AccountRes> getAll();
}
