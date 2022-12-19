package com.example.bank.services;

import com.example.bank.entities.Account;

import java.util.List;

public interface AccountService {

    Account addAccount(Account account);
    Account getAccount(Long id);
    List<Account> getAccountList();
    Account updateAccountInfo(Account account);
}
