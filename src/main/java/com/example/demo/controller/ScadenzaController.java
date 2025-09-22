package com.example.demo.controller;

import com.example.demo.config.AdminConfig;
import com.example.demo.dto.CategoriaTotaleDTO;
import com.example.demo.model.*;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

@Controller
@RequestMapping()
public class ScadenzaController {

    private final CaptchaValidator captchaValidator;
    private final ScadenzaService scadenzaService;
    private final RicevutaService ricevutaService;
    private final UserService userService;
    private final HtmlSanitizerService sanitizerService;
    private final EmailService emailService;
    private final AdminConfig adminConfig;
    private final SubscriptionRepository subscriptionRepository;
    private final AnaliticheSpeseService analiticheSpeseService;

    public ScadenzaController(EmailService emailService,
                              AdminConfig adminConfig,
                              SubscriptionRepository subscriptionRepository,
                              CaptchaValidator captchaValidator,
                              ScadenzaService scadenzaService,
                              RicevutaService ricevutaService,
                              UserService userService,
                              HtmlSanitizerService sanitizerService,
                              AnaliticheSpeseService analiticheSpeseService) {

        this.emailService = emailService;
        this.adminConfig = adminConfig;
        this.subscriptionRepository = subscriptionRepository;
        this.captchaValidator = captchaValidator;
        this.scadenzaService = scadenzaService;
        this.ricevutaService = ricevutaService;
        this.userService = userService;
        this.sanitizerService = sanitizerService;
        this.analiticheSpeseService = analiticheSpeseService;
    }
    @GetMapping("/scadenze/list")
    public String listScadenze(
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) String denominazione,
            @RequestParam(defaultValue = "0") int page, // Pagina corrente
            @RequestParam(defaultValue = "20") int size, // Elementi per pagina
            @RequestParam(defaultValue = "") String beneficiario, // Filtro per beneficiario
            @RequestParam(defaultValue = "beneficiario") String sortBy, // Campo di ordinamento
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(name = "message", required = false) String message,
            @RequestParam(name = "message1", required = false) String message1,
            Principal principal,
            Model model) {

        String loggedUsername = principal.getName();
        Register user = userService.loadRegisterByUsername(loggedUsername);
        List<Integer> anni = analiticheSpeseService.getAnniDisponibili();
        int annoSelezionato = (anno == null ? Year.now().getValue() : anno);
        Page<Scadenza> scadenze = analiticheSpeseService.getScadenzePageable(page, size, beneficiario, sortBy, sortDirection,annoSelezionato);
        // per combo in pagina grafico
        List<CategoriaTotaleDTO> data = analiticheSpeseService.getTotaliPerCategoriaAnno(annoSelezionato);
        model.addAttribute("anni", anni);
        model.addAttribute("annoSelezionato", annoSelezionato);
        model.addAttribute("labels", data.stream().map(CategoriaTotaleDTO::categoria).toList());
        model.addAttribute("values", data.stream().map(CategoriaTotaleDTO::totale).toList());
        model.addAttribute("scadenze", scadenze.getContent()
                .stream()
                .filter(scadenza -> scadenza.getBeneficiario().getUser().getId().equals(user.getId()))
                .peek(scadenza -> scadenza.setDataScadenzaFormattata(scadenza.getDataScadenza()))
                .peek(scadenza -> scadenza.setDataPagamentoFormattata(scadenza.getDataPagamento()))
                .peek(scadenza -> scadenza.setImportoFormattato(scadenza.getImporto()))
                .peek(scadenza -> scadenza.setGiorniRitardo((int) scadenza.differanzaGiorni()))
                .collect(Collectors.toList()));
        if (scadenze.getTotalElements() > 0) {
            scadenze.getContent().get(0).setGiorniRitardo((int) scadenze.getContent().get(0).differanzaGiorni());
        }
        model.addAttribute("anno", annoSelezionato);
        model.addAttribute("annoSelezionato", annoSelezionato);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", scadenze.getTotalPages());
        model.addAttribute("titleFilter", beneficiario);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("beneficiario", beneficiario);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);
        model.addAttribute("filter", denominazione);

        return "scadenze/list";
    }

    @GetMapping("/scadenza/new")
    public String showCreateForm(Model model,
                                 Principal principal) {
        Scadenza scadenza = new Scadenza();
        String username = principal.getName();  // lo username loggato
        Register user = userService.loadRegisterByUsername(username);
        List<Beneficiario> beneficiari = scadenzaService.findBeneficiariByIdUser(user.getId());
        model.addAttribute("scadenza", scadenza);
        model.addAttribute("beneficiari", beneficiari);
        return "scadenze/create";
    }

    @PostMapping("/scadenza/create")
    public String create(@RequestParam("g-recaptcha-response") String captchaResponse,
                         @RequestParam("denominazione") String denominazione,
                         @RequestParam("importo") BigDecimal importo,
                         @RequestParam("dataScadenza") LocalDate dataScadenza,
                         Principal principal,
                         Model model) {
        denominazione = sanitizerService.sanitize(denominazione);
        String username = principal.getName();  // lo username loggato
        Register user = userService.loadRegisterByUsername(username);
        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("message", "Captcha non valido. Riprova.");
            return "scadenze/create";// Torna alla pagina del form
        }
        if (denominazione == null || denominazione.isEmpty()) {
            model.addAttribute("message", "Il beneficiario è obbligatorio");
            return "scadenze/create";
        }
        if (dataScadenza == null) {
            model.addAttribute("message", "La data scadenza è obbligatoria");
            return "scadenze/create";
        }
        if (importo == null) {
            model.addAttribute("message", "L'importo è obbligatorio");
            return "scadenze/create";
        }
        Scadenza scadenza = new Scadenza();
        Beneficiario b = scadenzaService.findByBeneficiarioAndIdUser(denominazione, user);
        scadenza.setBeneficiario(b);
        scadenza.setDenominazione(b.getBeneficiario());
        scadenza.setDataScadenza(dataScadenza);
        scadenza.setImporto(importo);
        scadenza.setStatus("DA PAGARE");
        Scadenza s;
        try {
            s = scadenzaService.save(scadenza);
        } catch (Exception e) {
            model.addAttribute("message", "Errore nella creazione del beneficiario: " + e.getMessage());
            return "scadenze/create";
        }
        return "redirect:/" + s.getId() + "/editscadenza" + "?message1= Inserimento effettuato correttamente. Ora inserisci gli altri dati!";
    }


    @GetMapping("/{id}/editscadenza")
    public String editScadenza(@PathVariable("id") Integer id,
                               @RequestParam(name = "message", required = false) String message,
                               @RequestParam(name = "message1", required = false) String message1,
                               Model model,
                               Principal principal) {
        Scadenza scadenza = scadenzaService.findById(id);
        scadenza.setStatus(scadenza.getStatus());
        if (scadenza == null) {
            // gestisci errore se non trovato
            return "security/access-denied";
        }
        List<Ricevuta> ricevute = ricevutaService.findRicevuteByIdScadenza(id);
        scadenza.setRicevute(ricevute);
        if (scadenza.getBeneficiario().getUser() == null) {
            return "security/access-denied";
        } else {
            String formattedDate = scadenza.getDataScadenza().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            model.addAttribute("formattedDataScadenza", formattedDate);
            if (scadenza.getDataPagamento() != null) {
                String formattedDatePagamento = scadenza.getDataPagamento().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                model.addAttribute("formattedDataPagamento", formattedDatePagamento);
            }
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
            String formattedImporto = currencyFormatter.format(scadenza.getImporto());
            model.addAttribute("formattedImporto", formattedImporto);
            scadenza.setGiorniRitardo((int) scadenza.differanzaGiorni());
            // L'utente loggato
            String loggedUsername = principal.getName();
            boolean isOwner = scadenza.getBeneficiario().getUser().getUsername().equals(loggedUsername);
            model.addAttribute("isOwner", isOwner);
            model.addAttribute("scadenzaForm", scadenza);
            model.addAttribute("message", message);
            model.addAttribute("message1", message1);
            return "scadenze/edit";
        }
    }

    @PostMapping(path = "/{idScadenza}/{status}/update")
    public String updateScadenza(@PathVariable("idScadenza") Integer idScadenza,
                                 @PathVariable("status") String status,
                                 @ModelAttribute("scadenzaForm") Scadenza scadenzaForm,
                                 Model model,
                                 Principal principal) {
        Scadenza scadenza = new Scadenza();
        Beneficiario beneficiario = scadenzaService.findById(idScadenza).getBeneficiario();
        scadenza.setBeneficiario(beneficiario);
        scadenza.setDenominazione(beneficiario.getBeneficiario());
        scadenza.setImporto(scadenzaForm.getImporto());
        scadenza.setDataScadenza(scadenzaForm.getDataScadenza());
        scadenza.setId(idScadenza);
        scadenza.setSollecito(scadenzaForm.getSollecito());
        scadenza.setGiorniRitardo((int) scadenzaForm.differanzaGiorni());
        scadenza.setDataPagamento(scadenzaForm.getDataPagamento());
        scadenza.setStatus(status);
        // L'utente loggato
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Register user = userService.loadRegisterByUsername(loggedUsername);
        // Verifico se il proprietario  è lo stesso che ha fatto login
        if (!user.getUsername().equals(loggedUsername)) {
            // se non sei il proprietario, redirect o errore
            return "security/access-denied";
        }
        try {
            String message = validazioni(scadenza);
            if (message != null) {
                model.addAttribute("message", message);
                return "redirect:/" + scadenza.getId() + "/edit" + "?message=" + message;
            }
            scadenzaService.update(scadenza);
            model.addAttribute("scadenza", scadenza);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/" + scadenza.getId() + "/editscadenza";
        }
        return "redirect:/scadenze/list?message=Scadenza aggiornato con successo!";
    }

    private String validazioni(Scadenza scadenza) {
        String message = null;
        if (scadenza.getImporto() == null) {
            message = "valorizzare l'importo";
            return message;
        }
        if (scadenza.getDataScadenza() == null) {
            message = "valorizzare la data di scadenza";
            return message;
        }
        if (scadenza.getBeneficiario() == null) {
            message = "valorizzare il beneficiario";
            return message;
        }
        return null;
    }

    @PostMapping("/{id}/deletescadenza")
    public String deleteScadenza(@PathVariable("id") Integer id, Principal principal, Model model) {
        Scadenza scadenza = scadenzaService.findById(id);
        Subscription s = subscriptionRepository.findByScadenza(scadenza);
        if (s != null) {
            return "redirect:/scadenze/list?message1=La scadenza risulta pagata. Impossibile cancellarla.";
        }
        String formattedDate = scadenza.getDataScadenza().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String loggedUsername = principal.getName(); // es: "mario rossi"
        // Verifico se il proprietario è lo stesso che ha fatto la login
        boolean isOwner = scadenza.getBeneficiario().getUser().getUsername().equals(loggedUsername);
        if (!isOwner) {
            // se non sei il proprietario, redirect o errore
            return "security/access-denied";
        }
        scadenzaService.deleteById(id);
        return "redirect:/" + id + "/" +formattedDate + "/infoscadenza";
    }

    @GetMapping(value = "/{id}/detailscadenza")
    public String beneficiariDetail(@PathVariable Integer id,
                                    @RequestParam(name = "message", required = false) String message,
                                    @RequestParam(name = "message1", required = false) String message1,
                                    Model model,
                                    Principal principal) {
        Scadenza scadenza = scadenzaService.findById(id);
        if (scadenza == null) {
            return "security/access-denied";// Gestione caso corso non trovato
        }
        List<Ricevuta> ricevute = ricevutaService.findRicevuteByIdScadenza(id);
        scadenza.setRicevute(ricevute);
        // L'utente loggato
        String loggedUsername = principal.getName();
        // Verifico se il proprietario  è lo stesso che ha fatto login
        boolean isOwner = (scadenza.getBeneficiario().getUser().getUsername().equals(loggedUsername));
        String formattedDate = scadenza.getDataScadenza().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        model.addAttribute("formattedDataScadenza", formattedDate);
        if (scadenza.getDataPagamento() != null) {
            String formattedDatePagamento = scadenza.getDataPagamento().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            model.addAttribute("formattedDataPagamento", formattedDatePagamento);
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        String formattedImporto = currencyFormatter.format(scadenza.getImporto());
        scadenza.setGiorniRitardo((int) scadenza.differanzaGiorni());
        model.addAttribute("formattedImporto", formattedImporto);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("scadenza", scadenza);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);
        return "scadenze/detail";

    }

    @GetMapping("/{id}/{dataScadenza}/infoscadenza")
    public String infoAdmin(@PathVariable Integer id,
                            @PathVariable String dataScadenza )
    {
          try {
              emailService.sendSimpleEmail(
                      adminConfig.getEmail(),
                      "Eliminazione Scadenza",
                      "La scadenza con identificativo " + id + " in scadenza il " + dataScadenza + " è stata eliminata."
              );
          } catch (Exception e) {
              return "redirect:/scadenze/list?message=Errore invio email all'amministratore: ! " + e.getMessage();
          }
          return "redirect:/scadenze/list?message=Scadenza eliminata con successo ed email all'amministratore inviata correttamente.";
    }
}
