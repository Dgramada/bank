package com.example.bank.services;

import com.example.bank.repositories.AccountRepository;
import com.example.bank.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImp implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImp(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account addAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountList() {
        return accountRepository.findAll();
    }

    @Override
    public Account updateAccountInfo(Account account, String name, String email) {
        return null;
    }

    @Override
    public Account updateAccountBalance(Account account, long accountId, BigDecimal amount) {
        return null;
    }

    @Override
    public void removeAccount(long accountId) {
        accountRepository.deleteById(accountId);
    }
}
