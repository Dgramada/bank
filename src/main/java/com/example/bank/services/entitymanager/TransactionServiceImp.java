package com.example.bank.services.entitymanager;

import com.example.bank.entities.Account;
import com.example.bank.entities.Transaction;
import com.example.bank.services.TransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImp implements TransactionService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Account senderAccount = getAccountFromDB(transaction.getSenderAccount().getId());
        Account recipientAccount = getAccountFromDB(transaction.getRecipientAccount().getId());
        if (transaction.getAmount().compareTo(senderAccount.getBalance()) > 0) {
            throw new IllegalArgumentException("Amount is larger than the sender's balance");
        }
        return createAndAddTransactionsToDB(senderAccount, recipientAccount, transaction.getAmount());
    }

    @Override
    @Transactional
    public Transaction createTransactionWithId(Long senderId, Long recipientId, BigDecimal amount) {
        Account sender = getAccountFromDB(senderId);
        Account recipient = getAccountFromDB(recipientId);
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalArgumentException("Amount is larger than the sender's balance");
        }
        return createAndAddTransactionsToDB(sender, recipient , amount);
    }

    @Override
    public Transaction getTransaction(Long tid) {
        TypedQuery<Transaction> query = entityManager.createQuery("SELECT t FROM Transaction t WHERE id = ?1",
                Transaction.class).setParameter(1, tid);
        if (Objects.isNull(query)) {
            throw new EntityNotFoundException("Transaction with id = " + tid + " was not found in the database");
        }
        return query.getSingleResult();
    }

    @Override
    public List<Transaction> getTransactionList() {
        TypedQuery<Transaction> query = entityManager.createQuery("SELECT t FROM Transaction t",
                Transaction.class);
        if (query.getResultList().isEmpty()) {
            throw new EntityNotFoundException("No transactions exist in the database");
        }
        return query.setMaxResults(100).getResultList();
    }

    private Transaction createAndAddTransactionsToDB(Account sender, Account recipient, BigDecimal amount) {
        Transaction transaction = new Transaction(sender, recipient, amount);
        entityManager.persist(transaction);
        Transaction secondTransaction = new Transaction(sender, sender, amount.negate());
        entityManager.persist(secondTransaction);
        setBalancesAfterTransaction(sender, recipient, amount);
        return entityManager.find(Transaction.class, transaction.getId());
    }

    private void setBalancesAfterTransaction(Account sender, Account recipient, BigDecimal amount) {
        recipient.setBalance(recipient.getBalance().add(amount));
        sender.setBalance(sender.getBalance().subtract(amount));
    }

    private Account getAccountFromDB(Long accountId) {
        Account account = entityManager.find(Account.class, accountId);
        if (Objects.isNull(account)) {
            throw new EntityNotFoundException("Account with id = " + accountId + " was not found in the database");
        }
        return account;
    }
}
