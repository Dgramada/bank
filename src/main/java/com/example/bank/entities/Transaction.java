package com.example.bank.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;
    private BigDecimal amount;
    @OneToOne
    @JoinColumn(name = "recipient_id")
    private Account recipient;
    @OneToOne
    @JoinColumn(name = "sender_id")
    private Account sender;
    private Date transactionDate = new Date();

    public Transaction(Account sender, Account recipient, BigDecimal amount) {
        this.amount = amount;
        this.recipient = recipient;
        this.sender = sender;
    }

    public Transaction() {

    }

    public Account getRecipient() {
        return recipient;
    }

    public void setRecipient(Account recipient) {
        this.recipient = recipient;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }


}
