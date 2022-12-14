package com.example.bank.controllers;

import com.example.bank.entities.Account;
import com.example.bank.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/createAccount")
    public Account createAccount(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @GetMapping("/accountList")
    public List<Account> getAccountList() {
        return accountService.getAccountList();
    }

    @DeleteMapping("/delete")
    public String deleteAccountById(@RequestParam(value = "id") long id) {
        accountService.removeAccount(id);
        return "Account was deleted successfully";
    }

    @PutMapping("/updateAccount")
    public Account updateAccount(@RequestBody Account account) {
        return accountService.updateAccountInfo(account, account.getName(), account.getEmail(), account.getAddress());
    }
}
