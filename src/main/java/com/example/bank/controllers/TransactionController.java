package com.example.bank.controllers;

import com.example.bank.dto.TransactionDTO;
import com.example.bank.entities.Transaction;
import com.example.bank.services.TransactionServiceImp;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    private final TransactionServiceImp transactionServiceImp;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionController(TransactionServiceImp transactionServiceImp, ModelMapper modelMapper) {
        this.transactionServiceImp = transactionServiceImp;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/transaction")
    public TransactionDTO createTransaction(@RequestBody Transaction transaction) {
        return modelMapper.map(transactionServiceImp.createTransaction(transaction), TransactionDTO.class);
    }

    @PostMapping("/transaction_with_id")
    public TransactionDTO createTransactionWithId(@RequestParam(value = "recipientId") Long recipientId,
                                         @RequestParam(value = "senderId") Long senderId,
                                         @RequestParam(value = "amount") BigDecimal amount) {
        return modelMapper.map(transactionServiceImp.createTransactionWithId(recipientId, senderId, amount), TransactionDTO.class);
    }

    @GetMapping("/transactionList")
    public List<TransactionDTO> getTransactionList() {
        return transactionServiceImp.getTransactionList().parallelStream().
                map(transaction -> modelMapper.map(transaction, TransactionDTO.class)).
                collect(Collectors.toList());
    }

    @GetMapping("/transaction")
    public TransactionDTO getTransaction(@RequestParam(value = "id") Long id) {
        return modelMapper.map(transactionServiceImp.getTransaction(id), TransactionDTO.class);
    }
}
