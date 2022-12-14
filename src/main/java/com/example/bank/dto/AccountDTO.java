package com.example.bank.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class AccountDTO {

    private Long id;
    private Date date;
    private BigDecimal balance;
    private String name;
    private String email;
    private String address;
}
