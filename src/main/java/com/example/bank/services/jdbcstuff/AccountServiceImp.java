package com.example.bank.services.jdbcstuff;

import com.example.bank.entities.Account;
import com.example.bank.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AccountServiceImp implements AccountService {

    private final DataSource dataSource;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error("Fatal error. Cannot load driver.", e);
        }
    }

    @Autowired
    public AccountServiceImp(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account addAccount(Account account) {
        account.setDate(new Date());
        java.sql.Date sqlDate = new java.sql.Date(account.getDate().getTime());
        account.setDate(new Date());
        if (Objects.isNull(account.getBalance())) {
            account.setBalance(new BigDecimal(0));
        }
        String query = "INSERT INTO account(name, address, date, email, balance) VALUES(?, ?, ?, ?, ?)";
        String queryForLastId = "SELECT id FROM account ORDER BY id DESC LIMIT 1";
        int param = 1;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             PreparedStatement id_statement = connection.prepareStatement(queryForLastId)) {
            preparedStatement.setString(param++, account.getName());
            preparedStatement.setString(param++, account.getAddress());
            preparedStatement.setDate(param++, sqlDate);
            preparedStatement.setString(param++, account.getEmail());
            preparedStatement.setObject(param, account.getBalance());
            preparedStatement.executeUpdate();
            ResultSet rs = id_statement.executeQuery();
            rs.next();
            Long id = rs.getLong(1);
            account.setId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public Account getAccount(Long id) {
        String query = "SELECT * FROM account WHERE id = ?";
        Account account = new Account();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                account.setId(rs.getLong(1));
                account.setDate(rs.getDate(2));
                account.setAddress(rs.getString(3));
                account.setBalance(rs.getBigDecimal(4));
                account.setEmail(rs.getString(5));
                account.setName(rs.getString(6));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public List<Account> getAccountList() {
        List<Account> accountList = new ArrayList<>();
        String query = "SELECT * FROM account LIMIT 100";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();
            while (rs.next()) {
                Account account = new Account();
                accountList.add(account);
                account.setId(rs.getLong("id"));
                account.setDate(rs.getDate("date"));
                account.setAddress(rs.getString("address"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setEmail(rs.getString("email"));
                account.setName(rs.getString("name"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accountList;
    }

    @Override
    public Account updateAccountInfo(Account account) {
        String query = "UPDATE account SET name = ?, email = ?, address = ? WHERE id = ?";
        String infoQuery = "SELECT * FROM account WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                PreparedStatement accountInfoStatement = connection.prepareStatement(infoQuery)) {
            int param = 1;
            preparedStatement.setString(param++, account.getName());
            preparedStatement.setString(param++, account.getEmail());
            preparedStatement.setString(param++, account.getAddress());
            preparedStatement.setLong(param, account.getId());
            preparedStatement.executeUpdate();
            accountInfoStatement.setLong(1, account.getId());
            ResultSet rs = accountInfoStatement.executeQuery();
            while (rs.next()) {
                account.setBalance(rs.getBigDecimal("balance"));
                account.setDate(rs.getDate("date"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return account;
    }
}
