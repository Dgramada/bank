package com.example.bank.controllers;

import com.example.bank.entities.Transaction;
import com.example.bank.services.TransactionServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    private final TransactionServiceImp transactionServiceImp;

    @Autowired
    public TransactionController(TransactionServiceImp transactionServiceImp) {
        this.transactionServiceImp = transactionServiceImp;
    }

    @PostMapping("/transaction")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionServiceImp.createTransaction(transaction);
    }

    @GetMapping("/transactionList")
    public List<Transaction> getTransactionList() {
        return transactionServiceImp.getTransactionList();
    }

    @GetMapping("/transaction")
    public Transaction getTransaction(@RequestParam(value = "id") Long id) {
        return transactionServiceImp.getTransaction(id);
    }


}
