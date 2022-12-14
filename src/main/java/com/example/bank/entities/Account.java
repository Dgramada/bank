package com.example.bank.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
