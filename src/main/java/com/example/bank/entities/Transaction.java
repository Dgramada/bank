package com.example.bank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction")
public class Transaction  extends BaseEntity {

    @Getter
    @Setter
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @Getter
    private Account recipientAccount;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    @Getter
    private Account senderAccount;

    public Transaction(Account senderAccount, Account recipientAccount, BigDecimal amount) {
        this.amount = amount;
        this.recipientAccount = recipientAccount;
        this.senderAccount = senderAccount;
    }

    public Transaction() {

    }
}
