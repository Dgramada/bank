package com.example.bank.services;

import com.example.bank.entities.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);
    Transaction createTransactionWithId(Long senderId, Long recipientId, BigDecimal amount);
    Transaction getTransaction(Long tid);
    List<Transaction> getTransactionList();
}
