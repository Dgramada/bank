package com.example.bank.controllers;

import com.example.bank.dto.TransactionDTO;
import com.example.bank.entities.Transaction;
//import com.example.bank.services.usingrepo.TransactionServiceImp;
import com.example.bank.services.entitymanager.TransactionServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    private final TransactionServiceImp transactionService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionController(TransactionServiceImp transactionService, ModelMapper modelMapper) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
    }

    /**
     * Create a transaction by providing a body for the transaction object.
     * @param transaction a transaction object entered through a body
     * @return the newly created transaction entered through the parameter
     */
    @PostMapping("/transaction")
    public TransactionDTO createTransaction(@RequestBody Transaction transaction) {
        return modelMapper.map(transactionService.createTransaction(transaction), TransactionDTO.class);
    }

    /**
     * Create a transaction using the ids of the sender and the recipient which are
     * entered through
     * @param senderId the id of the sender account
     * @param recipientId the id of the recipient account
     * @param amount the amount that will be transferred between the accounts
     * @return the transaction between the recipient and the sender
     */
    @PostMapping("/transaction_with_id")
    public TransactionDTO createTransactionWithId(@RequestParam(value = "senderId") Long senderId, @RequestParam(value = "recipientId") Long recipientId,
                                                  @RequestParam(value = "amount") BigDecimal amount) {
        return modelMapper.map(transactionService.createTransactionWithId(senderId, recipientId, amount), TransactionDTO.class);
    }

    /**
     * Get a list of all the transactions as DTOs
     * @return a list with all the transactions as DTOs
     */
    @GetMapping("/transactionList")
    public List<TransactionDTO> getTransactionList() {
        return transactionService.getTransactionList().parallelStream().
                map(transaction -> modelMapper.map(transaction, TransactionDTO.class)).
                collect(Collectors.toList());
    }

    /**
     * Get the transaction with the specified id as a DTO.
     * @param id the id of the transaction
     * @return the transaction as a DTO
     */
    @GetMapping("/transaction")
    public TransactionDTO getTransaction(@RequestParam(value = "id") Long id) {
        return modelMapper.map(transactionService.getTransaction(id), TransactionDTO.class);
    }
}
