package com.example.bank.services.entitymanager;

import com.example.bank.entities.Account;
import com.example.bank.services.AccountService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AccountServiceImp implements AccountService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Account addAccount(Account account) {
        entityManager.persist(account);
        return entityManager.find(Account.class, account.getId());
    }

    @Override
    public Account getAccount(Long id) {
        TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a WHERE id = ?1",
                        Account.class).setParameter(1, id);
        if (query.setMaxResults(1).getResultList().size() == 0) {
            throw new EntityNotFoundException("Account id = " + id + " not found in the database");
        }
        return query.getSingleResult();
    }

    @Override
    public List<Account> getAccountList() {
        TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a", Account.class);
        if (query.getResultList().isEmpty()) {
            throw new EntityNotFoundException("No accounts exists in the database");
        }
        return query.setMaxResults(100).getResultList();
    }

    @Override
    @Transactional
    public Account updateAccountInfo(Account account) {
        if (Objects.isNull(entityManager.find(Account.class, account.getId()))) {
            throw new EntityNotFoundException("Account with id = " + account.getId() + " does not exist");
        }
        Account dbAccount = entityManager.find(Account.class, account.getId());
        dbAccount.setName(account.getName());
        dbAccount.setEmail(account.getEmail());
        dbAccount.setAddress(account.getAddress());
        return dbAccount;
    }
}