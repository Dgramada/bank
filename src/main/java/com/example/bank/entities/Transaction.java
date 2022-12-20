package com.example.bank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction  extends BaseEntity {

    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Account recipientAccount;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account senderAccount;

    public Transaction(Account senderAccount, Account recipientAccount, BigDecimal amount) {
        this.amount = amount;
        this.recipientAccount = recipientAccount;
        this.senderAccount = senderAccount;
    }
}
