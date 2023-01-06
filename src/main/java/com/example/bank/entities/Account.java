package com.example.bank.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@ToString
@Table(name = "account")
public class Account extends BaseEntity {
    private String name;
    @Column(columnDefinition="DECIMAL(38,2) DEFAULT '0.00'")
    private BigDecimal balance = BigDecimal.ZERO;
    private String email;
    private String address;
}
