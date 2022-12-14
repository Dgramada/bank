package com.example.bank.account;

import com.example.bank.entities.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account addAccount(Account account);
    List<Account> getAccountList();
    Account updateAccountInfo(Account account, String name, String email);
    Account updateAccountBalance(Account account, long accountId, BigDecimal amount);
    void removeAccount(long id);
}
