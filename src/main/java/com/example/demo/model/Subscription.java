package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "Subscription")
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Register user;

    @ManyToOne
    @JoinColumn(name = "scadenza_id")
    private Scadenza scadenza;
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    @Column(name = "paid_amount")
    private BigDecimal paidAmount;
    @Column(name = "paid_currency")
    private String paidCurrency;
    @Column(name = "payment_type")
    private String paymentType;
    @Column(name = "transaction_id")
    private String transactionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Register getUser() {
        return user;
    }

    public void setUser(Register user) {
        this.user = user;
    }

    public Scadenza getScadenza() {
        return scadenza;
    }

    public void setScadenza(Scadenza scadenza) {
        this.scadenza = scadenza;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(String paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}

