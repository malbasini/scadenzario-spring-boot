package com.example.demo.controller;

import com.example.demo.model.Scadenza;
import com.example.demo.model.Subscription;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ScadenzaService;
import com.example.demo.service.StripeService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/stripe")
public class StripeController {
    @Autowired
    private StripeService stripeService;
    @Autowired
    private ScadenzaService scadenzaService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/{scadenzaId}/checkout")
    public String startCheckout(@PathVariable("scadenzaId") Integer scadenzaId,
                                Principal principal,
                                HttpSession session,
                                Model model) {
        // Parametri fissi o recuperati da form/DB
        String loggedUsername = principal.getName();
        Integer userId = userRepository.findByUsername(loggedUsername).getId();
        Scadenza scadenza = scadenzaService.findById(scadenzaId);
        session.setAttribute("userId", userId);
        session.setAttribute("scadenzaId", scadenzaId);
        BigDecimal amount = new BigDecimal(String.valueOf(scadenza.getImporto().doubleValue()));
        String currency = "EUR";
        String successUrl = "https://localhost:8443/stripe/success";
        String cancelUrl = "https://localhost:8443/stripe/cancel";
        try {
            String checkoutUrl = String.valueOf(stripeService.createCheckoutSession(userId, scadenzaId, amount, currency, successUrl, cancelUrl
            ));
            // reindirizza lo user alla pagina di pagamento ospitata da Stripe
            return "redirect:" + checkoutUrl;
        } catch (StripeException e) {
            model.addAttribute("message", e.getMessage());
            return "stripe/error";
        }
    }
    @GetMapping("/success")
    public String success(@RequestParam("session_id") String sessionId,
                          Model model,
                          HttpSession httpSession) {
        try {
            Subscription subscription = stripeService.handleCheckoutSession(sessionId,httpSession);
            if (subscription != null) {
                //AGGIORNO LA DATA PAGMENTO E LO STATUS DELLA SCADENZA
                int scadenzaId = (int) httpSession.getAttribute("scadenzaId");
                Scadenza scadenza = scadenzaService.findById(scadenzaId);
                scadenza.setDataPagamento(LocalDate.now());
                scadenza.setStatus("pagato");
                scadenza.setGiorniRitardo((int) scadenza.differanzaGiorni());
                scadenzaService.save(scadenza);
                model.addAttribute("subscription", subscription);
                return "stripe/paymentsuccess";
            }
        } catch (StripeException e) {
            model.addAttribute("message", e.getMessage());
        }
        return "stripe/error";
    }
    @GetMapping("/cancel")
    public String cancel() {
       //Gestisce il pagamento annullato
        return "stripe/paymentcancelled";
    }
/*
 * Esempio (opzionale) di endpoint Webhook se vuoi gestire in automatico
 * la conferma del pagamento Stripe, invece di passare da /success.
 */
// @PostMapping("/webhook")
// public ResponseEntity<String> handleStripeWebhook(HttpServletRequestrequest) {
// // Leggi e valida l'evento (Stripe-Signature) e aggiorna DB diconseguenza
// return ResponseEntity.ok("ok");
// }
}
