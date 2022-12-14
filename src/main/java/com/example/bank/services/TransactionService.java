package com.example.bank.services;

import com.example.bank.entities.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);
    Transaction getTransaction(Long tid);
    List<Transaction> getTransactionList();
}
