package com.example.demo.controller;

import com.example.demo.model.Scadenza;
import com.example.demo.model.Subscription;
import com.example.demo.model.Register;
import com.example.demo.service.ScadenzaService;
import jakarta.servlet.http.HttpSession;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PayPalService;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

/**
 * Controller classico Spring MVC (senza @RestController se si usano JSP).
 */
@Controller
@RequestMapping("/paypal")
public class PayPalController {

    private final PayPalService payPalService;
    private final UserRepository userRepository;
    private final ScadenzaService scadenzaService;

    public PayPalController(PayPalService payPalService,
                            UserRepository userRepository,
                            ScadenzaService scadenzaService) {

        this.payPalService = payPalService;
        this.userRepository = userRepository;
        this.scadenzaService = scadenzaService;
    }

    // Esempio di rotta per avviare il pagamento
    @GetMapping("/{scadenzaId}/pay")
    public String payWithPayPal(@PathVariable("scadenzaId") Integer scadenzaId,
                                Principal principal,
                                Model model,
                                HttpSession session)
     {
        String loggedUsername = principal.getName();
        Register user = userRepository.findByUsername(loggedUsername);
        Scadenza scadenza = scadenzaService.findById(scadenzaId);
        int userId = user.getId();
        session.setAttribute("userId", userId);
        session.setAttribute("scadenzaId", scadenzaId);
         // Parametri fissi o recuperati da form
        BigDecimal amount = scadenza.getImporto();
        String currency = "EUR";
        String description = scadenza.getBeneficiario().getDescrizione();
        String successUrl = "https://localhost:8443/paypal/success";
        String cancelUrl = "https://localhost:8443/paypal/cancel";
        try {
                String approvalLink = payPalService.createPayment(
                    user,
                    scadenza,
                    amount,
                    currency,
                    description,
                    successUrl,
                    cancelUrl
                );
                // redirect su PayPal
                return "redirect:" + approvalLink;
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            // Gestione errore
            model.addAttribute("message", e.getMessage());
            return "paypal/error";
        }
    }
    // Rotta di successo definita come returnUrl in createPayment
    @GetMapping("/success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             Model model,
                             HttpSession httpSession) {
        try {
            Subscription subscription = payPalService.executePayment(paymentId, payerId, httpSession);
            if (subscription != null) {
                // Pagamento completato con successo:
                // mostra una pagina di conferma, ad es. "paymentSuccess"
                model.addAttribute("subscription", subscription);
                //AGGIORNO LA DATA PAGMENTO E LO STATUS DELLA SCADENZA
                int scadenzaId = (int) httpSession.getAttribute("scadenzaId");
                Scadenza scadenza = scadenzaService.findById(scadenzaId);
                scadenza.setDataPagamento(LocalDate.now());
                scadenza.setStatus("pagato");
                scadenza.setGiorniRitardo((int) scadenza.differanzaGiorni());
                scadenzaService.save(scadenza);
                return "paypal/paymentsuccess";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            model.addAttribute("message", e.getMessage());
        }
        return "paypal/error";
    }
    // Rotta di annullamento definita come cancelUrl
    @GetMapping("/cancel")
    public String cancelPay() {
        // gestisci l'annullamento
        return "paypal/paymentcancelled";
    }
}