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
        Account sender = getAccountFromDB(transaction.getSenderAccount().getId());
        Account recipient = getAccountFromDB(transaction.getRecipientAccount().getId());
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient balance in the account");
        }
        return createAndSaveTransactionsToDB(sender, recipient, amount);
    }

    @Override
    @Transactional
    public Transaction createTransactionWithId(Long senderId, Long recipientId, BigDecimal amount) {
        Account sender = getAccountFromDB(senderId);
        Account recipient = getAccountFromDB(recipientId);
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

    /**
     * Get the account from the database with accountId as id and throw exception if it is not present
     * @param accountId the id of the account we want to return from the database
     * @return returns an account entity from the database with id = accountId
     */
    private Account getAccountFromDB(Long accountId) {
        Optional<Account> accountDatabase = accountRepository.findById(accountId);
        if (accountDatabase.isEmpty()) {
            throw new EntityNotFoundException("Sender is not present in the database");
        }
        return accountDatabase.get();
    }
}
