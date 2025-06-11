package com.example.demo.service;

import com.example.demo.model.Subscription;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpSession;

@Service
public class StripeService {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * Esempio di metodo per finalizzare il pagamento
     * (se usi i Webhook, puoi spostare questa logica lì).
     */
    public Subscription handleCheckoutSession(String sessionId, HttpSession httpSession) throws StripeException {
        // Recupera la Session da Stripe
        Session session = Session.retrieve(sessionId);
        if (session.getPaymentStatus().equals("paid"))
        {
             // Pagamento completato con successo
            // Esempio: recupero userId e courseId da metadati (se li hai salvati)
            // oppure da sessione; qui è dimostrativo
            int userId = (int) httpSession.getAttribute("userId");
            int scadenzaId = (int) httpSession.getAttribute("scadenzaId");
            if (httpSession.getAttribute("userId") == null || httpSession.getAttribute("scadenzaId") == null) {
                throw new IllegalStateException("I dettagli della sessione non sono stati trovati.");
            }
            // Ricava l'importo e la valuta dalla PaymentIntent
            String paymentIntentId = session.getPaymentIntent();
           // Con la PaymentIntent possiamo recuperare l’importo effettivo:
           PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
           BigDecimal amountPaid = new BigDecimal(pi.getAmountReceived()).divide(BigDecimal.valueOf(100));
           String currency = pi.getCurrency();
           // Oppure, semplificando (non sempre 1:1 con la Session):
            return subscriptionService.createSubscriptionStripe(
                    userId,
                    scadenzaId,
                    amountPaid,
                    currency,
                    "STRIPE",
                    paymentIntentId
            );
        }
        return null;
    }
    /**
     * Crea una Session di Checkout su Stripe e restituisce l'URL di
     redirezione.
     */
    public String createCheckoutSession(Integer userId, Integer scadenzaId,
                                        BigDecimal amount,
                                        String currency, String successUrl,
                                        String cancelUrl)
            throws StripeException {
            // L'amount per Stripe è in centesimi (long).
            long amountCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amountCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("scadenza #" + scadenzaId) // O un nome più descrittivo
                                                                                .build()
                                                                )
                                                                .build()
                                                )
                                                .setQuantity(1L)
                                                .build()
                                )
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.IDEAL)
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                                .setCancelUrl(cancelUrl)
                                .build();
        Session session = Session.create(params);
        return session.getUrl(); // Link di checkout
    }
}

