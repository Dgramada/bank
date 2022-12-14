package com.example.bank.controllers;

import com.example.bank.dto.AccountDTO;
import com.example.bank.entities.Account;
import com.example.bank.services.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountController(AccountService accountService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/createAccount")
    public AccountDTO createAccount(@RequestBody Account account) {
        return modelMapper.map(accountService.addAccount(account), AccountDTO.class);
    }

    @GetMapping("/accountList")
    public List<AccountDTO> getAccountList() {
        return accountService.getAccountList().parallelStream().
                map(account -> modelMapper.map(account, AccountDTO.class))
                .collect(Collectors.toList());
    }

    //TODO use modelMapper for delete account
    //TODO fix delete service because it does not work once a transaction is made for an account because of the relation
    @DeleteMapping("/delete")
    public String deleteAccountById(@RequestParam(value = "id") long id) {
        accountService.removeAccount(id);
        return "Account was deleted successfully";
    }

    @PutMapping("/updateAccount")
    public AccountDTO updateAccount(@RequestBody Account account) {
        return modelMapper.map(accountService.updateAccountInfo(account, account.getName(), account.getEmail(), account.getAddress()), AccountDTO.class);
    }
}
