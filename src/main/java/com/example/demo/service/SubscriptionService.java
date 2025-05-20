package com.example.demo.service;

import com.example.demo.model.Scadenza;
import com.example.demo.model.Subscription;
import com.example.demo.model.Register;

import java.math.BigDecimal;

public interface SubscriptionService {

    Subscription createSubscriptionPayPal(
            Integer userId,
            Integer scadenzaId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId);

    Subscription createSubscriptionStripe(
            Integer userId,
            Integer scadenzaId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId);

    void save(Subscription subscription);
}
