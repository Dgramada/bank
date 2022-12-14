package com.example.bank.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account extends BaseEntity {

    private String name;
    private BigDecimal balance = new BigDecimal(0);
    private String email;
    private String address;
}
