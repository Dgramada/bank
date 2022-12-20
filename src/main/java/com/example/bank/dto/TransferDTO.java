package com.example.bank.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferDTO {

    private Long senderId;
    private Long recipientId;
    private BigDecimal amount;
}
