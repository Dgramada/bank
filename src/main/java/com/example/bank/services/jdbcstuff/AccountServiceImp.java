package com.example.bank.services.jdbcstuff;

import com.example.bank.entities.Account;
import com.example.bank.services.AccountService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class AccountServiceImp implements AccountService {

    private final DataSource dataSource;

    @Autowired
    public AccountServiceImp(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error("Fatal error. Cannot load driver.", e);
        }
    }

    @Override
    public Account addAccount(@NotNull Account account) {
        String query = "SELECT * FROM account";
        long id;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
             ResultSet rs = preparedStatement.executeQuery()) {
            rs.moveToInsertRow();
            rs.updateString("name", account.getName());
            rs.updateString("address", account.getAddress());
            rs.updateString("email", account.getEmail());
            if (Objects.nonNull(account.getBalance())) {
                rs.updateBigDecimal("balance", account.getBalance());
            }
            rs.insertRow();
            id = rs.getLong("id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return getAccount(id);
    }

    @Override
    public Account getAccount(Long id) {
        Account account = new Account();
        String query = "SELECT id, date, name, balance, email, address FROM account WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    account = Account.builder().address(rs.getString("address")).
                            email(rs.getString("email")).
                            name(rs.getString("name")).
                            balance(rs.getBigDecimal("balance")).
                            id(rs.getLong("id")).
                            date(rs.getDate("date")).build();
                }
                return account;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Account> getAccountList() {
        List<Account> accountList = new LinkedList<>();
        String query = "SELECT id, date, name, balance, email, address FROM account LIMIT 100";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                Account account = Account.builder().address(rs.getString("address")).
                        email(rs.getString("email")).
                        name(rs.getString("name")).
                        balance(rs.getBigDecimal("balance")).
                        id(rs.getLong("id")).
                        date(rs.getDate("date")).build();
                accountList.add(account);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accountList;
    }

    @Override
    public Account updateAccountInfo(Account account) {
        String query = "SELECT id, date, name, balance, email, address FROM account WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            preparedStatement.setLong(1, account.getId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                rs.updateString("name", account.getName());
                rs.updateString("address", account.getAddress());
                rs.updateString("email", account.getEmail());
                rs.updateRow();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return getAccount(account.getId());
    }
}
