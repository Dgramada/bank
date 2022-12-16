package com.example.bank.services;

import com.example.bank.entities.Account;
import com.example.bank.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Account getAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new EntityNotFoundException("Account with id = " + id + " was not found in the database");
        }
        return accountRepository.getReferenceById(id);
    }

    @Override
    public List<Account> getAccountList() {
        if (accountRepository.findAll().isEmpty()) {
            throw new EntityNotFoundException("No accounts exist in the database");
        }
        return accountRepository.findAll();
    }

    @Override
    public Account updateAccountInfo(Account account, String name, String email, String address) {
        Optional<Account> accountDB = accountRepository.findById(account.getId());
        if (accountDB.isEmpty()) {
            throw new EntityNotFoundException("Account with id = " + account.getId() + " was not found in the database");
        }
        accountDB.get().setName(name);
        accountDB.get().setEmail(email);
        accountDB.get().setAddress(address);
        accountRepository.save(accountDB.get());
        return accountDB.get();
    }
}
