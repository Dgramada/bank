package com.example.bank.services.usingrepo;

import com.example.bank.entities.Account;
import com.example.bank.entities.Transaction;
import com.example.bank.repositories.AccountRepository;
import com.example.bank.repositories.TransactionRepository;
import com.example.bank.services.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<Account> senderDatabase = accountRepository.findById(transaction.getSenderAccount().getId());
        Optional<Account> recipientDatabase = accountRepository.findById(transaction.getRecipientAccount().getId());
        if (senderDatabase.isEmpty()) {
            throw new EntityNotFoundException("Sender is not present in the database");
        }
        Account sender = senderDatabase.get();
        if (recipientDatabase.isEmpty()) {
            throw new EntityNotFoundException("Recipient is not present in the database");
        }
        Account recipient = recipientDatabase.get();
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient balance in the account");
        }
        return createAndSaveTransactionsToDB(sender, recipient, amount);
    }

    @Override
    @Transactional
    public Transaction createTransactionWithId(Long recipientId, Long senderId, BigDecimal amount) {
        Optional<Account> senderDatabase = accountRepository.findById(senderId);
        Optional<Account> recipientDatabase = accountRepository.findById(recipientId);
        if (senderDatabase.isEmpty()) {
            throw new EntityNotFoundException("Sender is not present in the database");
        }
        Account sender = senderDatabase.get();
        if (recipientDatabase.isEmpty()) {
            throw new EntityNotFoundException("Recipient is not present in the database");
        }
        Account recipient = recipientDatabase.get();
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient balance in the account: " +
                    "amount = " + amount + " > " + " sender = " + sender.getBalance());
        }
        return createAndSaveTransactionsToDB(sender, recipient, amount);
    }

    @Override
    public Transaction getTransaction(Long tid) {
        if (!transactionRepository.existsById(tid)) {
            throw new EntityNotFoundException("Transaction with id = " + tid + " was not found in the database");
        }
        return transactionRepository.getReferenceById(tid);
    }

    @Override
    public List<Transaction> getTransactionList() {
        if (transactionRepository.findAll().isEmpty()) {
            throw new EntityNotFoundException("No transactions are present in the database");
        }
        return transactionRepository.findAll();
    }

    private Transaction createAndSaveTransactionsToDB(Account sender, Account recipient, BigDecimal amount) {
        setBalancesAfterTransaction(sender, recipient, amount);
        Transaction senderTransaction = new Transaction(sender, recipient, amount);
        Transaction recipientTransaction = new Transaction(sender, sender, amount.multiply(new BigDecimal(-1)));
        transactionRepository.save(senderTransaction);
        transactionRepository.save(recipientTransaction);
        return recipientTransaction;
    }

    private void setBalancesAfterTransaction(Account sender, Account recipient, BigDecimal amount) {
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));
    }
}
