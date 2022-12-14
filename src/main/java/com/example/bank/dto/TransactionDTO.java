package com.example.bank.dto;

import com.example.bank.entities.Account;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TransactionDTO {

    private Long id;
    private Date date;
    private Account senderAccount;
    private Account recipientAccount;
    private BigDecimal amount;
}
