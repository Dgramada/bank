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

    /**
     * Create an account with a request body.
     * @param account the account that should be created
     * @return the new account
     */
    @PostMapping("/createAccount")
    public AccountDTO createAccount(@RequestBody Account account) {
        return modelMapper.map(accountService.addAccount(account), AccountDTO.class);
    }

    /**
     * GET the account with the specified id as a DTO
     * @param id the id of the account
     * @return an account DTO
     */
    @GetMapping("/getAccount")
    public AccountDTO getAccount(@RequestParam(value = "id") Long id) {
        return modelMapper.map(accountService.getAccount(id), AccountDTO.class);
    }

    /**
     * Get a DTO list containing all the accounts from the database.
     * @return list containing the accounts
     */
    @GetMapping("/accountList")
    public List<AccountDTO> getAccountList() {
        return accountService.getAccountList().parallelStream().
                map(account -> modelMapper.map(account, AccountDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Update the mutable account information(name, email, address) for an account.
     * @param account the account that is being updated
     * @return the updated account as a DTO object
     */
    @PutMapping("/updateAccount")
    public AccountDTO updateAccount(@RequestBody Account account) {
        return modelMapper.map(accountService.updateAccountInfo(account, account.getName(),
                account.getEmail(), account.getAddress()), AccountDTO.class);
    }
}
