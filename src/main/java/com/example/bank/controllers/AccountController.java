package com.example.bank.controllers;

import com.example.bank.dto.AccountDTO;
import com.example.bank.entities.Account;
//import com.example.bank.services.usingrepo.AccountServiceImp;
//import com.example.bank.services.entitymanager.AccountServiceImp;
import com.example.bank.services.jdbcstuff.AccountServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    private final AccountServiceImp accountService;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountController(AccountServiceImp accountService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    /**
     * Create an account with a request body.
     * @param accountDTO the account that should be created
     * @return the new account
     */
    @PostMapping("/createAccount")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        Account account = modelMapper.map(accountDTO, Account.class);
        AccountDTO responseDTO = modelMapper.map(accountService.addAccount(account), AccountDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * GET the account with the specified id as a DTO
     * @param id the id of the account
     * @return an account DTO
     */
    @GetMapping("/getAccount")
    public ResponseEntity<AccountDTO> getAccount(@RequestParam(value = "id") Long id) {
        AccountDTO responseDTO = modelMapper.map(accountService.getAccount(id), AccountDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get a DTO list containing all the accounts from the database.
     * @return list containing the accounts
     */
    @GetMapping("/accountList")
    public ResponseEntity<List<AccountDTO>> getAccountList() {
        List<AccountDTO> responseListDTO = accountService.getAccountList().parallelStream().
                map(account -> modelMapper.map(account, AccountDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /**
     * Update the mutable account information(name, email, address) for an account.
     * @param accountDTO the account that is being updated
     * @return the updated account as a DTO object
     */
    @PutMapping("/updateAccount")
    public ResponseEntity<AccountDTO> updateAccount(@RequestBody AccountDTO accountDTO) {
        Account account = modelMapper.map(accountDTO, Account.class);
        AccountDTO responseDTO = modelMapper.map(accountService.updateAccountInfo(account), AccountDTO.class);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
