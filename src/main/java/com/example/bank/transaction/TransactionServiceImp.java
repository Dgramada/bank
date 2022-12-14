package com.example.bank.transaction;

import com.example.bank.entities.Account;
import com.example.bank.account.AccountRepository;
import com.example.bank.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImp implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    @Autowired
    public TransactionServiceImp(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        Optional<Account> senderDatabase = accountRepository.findById(transaction.getSender().getAid());
        Optional<Account> recipientDatabase = accountRepository.findById(transaction.getRecipient().getAid());

        if (!senderDatabase.isPresent()) {
            throw new IllegalStateException("Sender is not present in the database");
        }
        Account sender = senderDatabase.get();
        if (!recipientDatabase.isPresent()) {
            throw new IllegalStateException("Recipient is not present in the database");
        }
        Account recipient = recipientDatabase.get();

        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalStateException("Insufficient balance in the account");
        }
        sender.setBalance(transaction.getSender().getBalance().subtract(amount));
        recipient.setBalance(transaction.getRecipient().getBalance().add(amount));
        transactionRepository.save(transaction);
        Transaction transaction1 = new Transaction(transaction.getRecipient(), transaction.getSender(), amount.multiply(new BigDecimal(-1)));
        transactionRepository.save(transaction1);
        return transaction;
    }

    @Override
    public Transaction getTransaction(Long tid) {
        return transactionRepository.getReferenceById(tid);
    }

    @Override
    public List<Transaction> getTransactionList() {
        return transactionRepository.findAll();
    }
}
