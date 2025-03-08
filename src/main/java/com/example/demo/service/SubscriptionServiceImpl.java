package com.example.demo.service;

import com.example.demo.model.Scadenza;
import com.example.demo.model.Subscription;
import com.example.demo.model.Register;
import com.example.demo.repository.ScadenzeRepository;

import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScadenzeRepository scadenzaRepository;

    @Override
    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    /**
     * Crea (o aggiorna) l'iscrizione dopo il pagamento avvenuto.
     *
     * @param userId        lo studente
     * @param scadenzaId    la scadenza
     * @param amount        importo pagato
     * @param currency      valuta
     * @param paymentType   "PAYPAL" o "STRIPE"
     * @param transactionId l'ID transazione restituito dal gateway
     * @return l'oggetto Subscription salvato
     */
    public Subscription createSubscriptionPayPal(
            Integer userId,
            Integer scadenzaId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId) {
        paymentType="paypal";
        Subscription subscription = new Subscription();
        Register user = userRepository.findById(userId).orElse(null);
        subscription.setUser(user);
        Scadenza scadenza = scadenzaRepository.findById(scadenzaId).orElse(null);
        subscription.setScadenza(scadenza);
        subscription.setPaymentDate(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
        subscription.setPaidAmount(amount);
        subscription.setPaidCurrency(currency);
        subscription.setPaymentType(paymentType);
        subscription.setTransactionId(transactionId);
        Subscription s = subscriptionRepository.save(subscription);
        return s;
    }
    public Subscription createSubscriptionStripe(
            Integer userId,
            Integer scadenzaId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId) {

        paymentType="stripe";

        Subscription subscription = new Subscription();
        Register user = userRepository.findById(userId).orElse(null);
        subscription.setUser(user);
        Scadenza scadenza = scadenzaRepository.findById(scadenzaId).orElse(null);
        subscription.setScadenza(scadenza);
        subscription.setPaymentDate(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
        subscription.setPaidAmount(amount);
        subscription.setPaidCurrency(currency);
        subscription.setPaymentType(paymentType);
        subscription.setTransactionId(transactionId);
        Subscription s = subscriptionRepository.save(subscription);
        return s;
    }
}
