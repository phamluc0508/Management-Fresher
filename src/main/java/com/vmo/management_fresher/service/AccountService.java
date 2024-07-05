package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.response.AccountRes;
import com.vmo.management_fresher.model.Account;

import java.util.List;

public interface AccountService {
    String createAccount(String uid, Account request);
    String updateAccount(String uid, String id, Account request);
    String deleteAccount(String id);
    Account addRoleAccount(String uid, String id, List<String> roles);
    AccountRes getById(String id);
    List<AccountRes> getAll();
}
