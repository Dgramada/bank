package com.example.bank.services.jdbcstuff;

import com.example.bank.entities.Account;
import com.example.bank.entities.Transaction;
import com.example.bank.services.TransactionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImp implements TransactionService {

    private final DataSource dataSource;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error("Fatal error. Cannot load driver.", e);
        }
    }

    @Autowired
    public TransactionServiceImp(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Transaction createTransaction(@NotNull Transaction transaction) {
        String transactionQuery = "INSERT INTO transaction(amount, recipient_id, sender_id) VALUES(?, ?, ?)";
        try (Connection connection = dataSource.getConnection()) {
            Account recipient = getAccountFromDB(transaction.getRecipientAccount().getId(), connection);
            Account sender = getAccountFromDB(transaction.getSenderAccount().getId(), connection);
            if (transaction.getAmount().compareTo(sender.getBalance()) > 0) {
                throw new IllegalArgumentException("Amount is larger than the sender's balance");
            }
            try (PreparedStatement preparedStatementTransaction = connection.prepareStatement(transactionQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                connection.setAutoCommit(false);
                int param = 1;
                preparedStatementTransaction.setBigDecimal(param++, transaction.getAmount());
                preparedStatementTransaction.setLong(param++, recipient.getId());
                preparedStatementTransaction.setLong(param, sender.getId());
                preparedStatementTransaction.executeUpdate();
                param = 1;
                preparedStatementTransaction.setBigDecimal(param++, transaction.getAmount().negate());
                preparedStatementTransaction.setLong(param++, sender.getId());
                preparedStatementTransaction.setLong(param, sender.getId());
                preparedStatementTransaction.executeUpdate();
                updateAccountBalance(recipient.getId(), transaction.getAmount(), connection);
                updateAccountBalance(sender.getId(), transaction.getAmount().negate(), connection);
                connection.commit();
                return getSecondToLastTransaction(connection);
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Transaction createTransactionWithId(Long senderId, Long recipientId, @NotNull BigDecimal amount) {
        String transactionQuery = "INSERT INTO transaction(amount, recipient_id, sender_id) VALUES(?, ?, ?)";
        try (Connection connection = dataSource.getConnection()) {
            if (amount.compareTo(getAccountFromDB(senderId, connection).getBalance()) > 0) {
                throw new IllegalArgumentException("Amount is larger than the sender's balance");
            }
            try (PreparedStatement createTransactionStatement = connection.prepareStatement(transactionQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                connection.setAutoCommit(false);
                int param = 1;
                createTransactionStatement.setBigDecimal(param++, amount);
                createTransactionStatement.setLong(param++, recipientId);
                createTransactionStatement.setLong(param, senderId);
                createTransactionStatement.executeUpdate();
                param = 1;
                createTransactionStatement.setBigDecimal(param++, amount.negate());
                createTransactionStatement.setLong(param++, senderId);
                createTransactionStatement.setLong(param, senderId);
                createTransactionStatement.executeUpdate();
                updateAccountBalance(recipientId, amount, connection);
                updateAccountBalance(senderId, amount.negate(), connection);
                connection.commit();
                return getSecondToLastTransaction(connection);
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Transaction getTransaction(Long tid) {
        String query = "SELECT * FROM transaction WHERE id = ?";
        Transaction transaction = new Transaction();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, tid);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            transaction.setId(rs.getLong("id"));
            transaction.setRecipientAccount(getAccountFromDB(rs.getLong("recipient_id"), connection));
            transaction.setSenderAccount(getAccountFromDB(rs.getLong("sender_id"), connection));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setDate(rs.getDate("date"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transaction;
    }

    @Override
    public List<Transaction> getTransactionList() {
        List<Transaction> transactionList = new ArrayList<>();
        String query = "SELECT * FROM transaction";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transactionList.add(transaction);
                transaction.setId(rs.getLong("id"));
                transaction.setRecipientAccount(getAccountFromDB(rs.getLong("recipient_id"), connection));
                transaction.setSenderAccount(getAccountFromDB(rs.getLong("sender_id"), connection));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setDate(rs.getDate("date"));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return transactionList;
    }

    @NotNull
    private  Account getAccountFromDB(Long id, Connection connection) {
        String query = "SELECT id, date, name, balance, email, address FROM account WHERE id = ?";
        Account account = new Account();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            account.setName(rs.getString("name"));
            account.setAddress(rs.getString("address"));
            account.setEmail(rs.getString("email"));
            account.setDate(rs.getDate("date"));
            account.setId(rs.getLong("id"));
            account.setBalance(rs.getBigDecimal("balance"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    private void updateAccountBalance(Long accountId, BigDecimal amount, Connection connection) {
        String query = "UPDATE account SET balance = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            Account account = getAccountFromDB(accountId, connection);
            preparedStatement.setBigDecimal(1, amount.add(account.getBalance()));
            preparedStatement.setLong(2, accountId);
            preparedStatement.executeUpdate();
            account.setBalance(amount.add(account.getBalance()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Transaction getSecondToLastTransaction(Connection connection) {
        String query = "SELECT * FROM transaction ORDER BY id DESC OFFSET 1 LIMIT 1";
        Transaction transaction = new Transaction();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            transaction.setId(rs.getLong("id"));
            transaction.setRecipientAccount(getAccountFromDB(rs.getLong("recipient_id"), connection));
            transaction.setSenderAccount(getAccountFromDB(rs.getLong("sender_id"), connection));
            transaction.setDate(rs.getDate("date"));
            transaction.setAmount(rs.getBigDecimal("amount"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transaction;
    }
}
