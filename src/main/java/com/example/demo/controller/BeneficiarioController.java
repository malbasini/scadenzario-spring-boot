package com.example.demo.controller;


import com.example.demo.config.AdminConfig;
import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import com.example.demo.model.Scadenza;
import com.example.demo.model.Subscription;
import com.example.demo.repository.BeneficiariRepository;
import com.example.demo.repository.ScadenzeRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.owasp.html.PolicyFactory;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BeneficiarioController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private CaptchaValidator captchaValidator;

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private UserService userService;

    @Autowired
    private HtmlSanitizerService sanitizerService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AdminConfig adminConfig;
    @Autowired
    private ScadenzaService scadenzaService;
    @Autowired
    private BeneficiariRepository beneficiariRepository;
    @Autowired
    private ScadenzeRepository scadenzeRepository;


    // GET /courses -> listing di tutti i corsi con supporto a paginazione, ricerca e ordinamento
    @GetMapping("/beneficiari/list")
    public String listBeneficiari(
            @RequestParam(defaultValue = "0") int page, // Pagina corrente
            @RequestParam(defaultValue = "10") int size, // Elementi per pagina
            @RequestParam(defaultValue = "") String beneficiario, // Filtro per beneficiario
            @RequestParam(defaultValue = "beneficiario") String sortBy, // Campo di ordinamento
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(name = "message", required = false) String message,
            @RequestParam(name = "message1", required = false) String message1,
            Principal principal,
            Model model) {

        String loggedUsername = principal.getName();
        Register user = userService.loadRegisterByUsername(loggedUsername);
        // Ottenere i beneficiari con paginazione, ricerca e ordinamento
        Page<Beneficiario> beneficiari = beneficiarioService.findBeneficiari(page, size, beneficiario, sortBy, sortDirection);


        model.addAttribute("beneficiari", beneficiari.getContent().stream()
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList()));

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", beneficiari.getTotalPages());
        model.addAttribute("titleFilter", beneficiario);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);

        return "beneficiari/list";
    }
    @GetMapping("/beneficiario/new")
    public String showCreateForm(Model model) {
        Beneficiario beneficiario = new Beneficiario();
        model.addAttribute("beneficiario", beneficiario);
        return "beneficiari/create";
    }
    @PostMapping("/beneficiario/create")
    public String create(@RequestParam("g-recaptcha-response") String captchaResponse,
                         @RequestParam String denominazione,
                         @RequestParam String description,
                         Principal principal,
                         Model model) {
        denominazione = sanitizerService.sanitize(denominazione);
        description = sanitizerService.sanitize(description);
        String username = principal.getName();  // lo username loggato
        Register user = userService.loadRegisterByUsername(username);
        if(!verificaDenominazione(denominazione,description,user,model)) {
            this.valorizzaInput(model,denominazione,description);
            return "beneficiari/create";
        }
        this.valorizzaInput(model,denominazione,description);
        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("message", "Captcha non valido. Riprova.");
            return "beneficiari/create";// Torna alla pagina del form
        }
        if(denominazione==null || denominazione.isEmpty()){
            model.addAttribute("message", "Il beneficiario è obbligatorio");
            return "beneficiari/create";
        }
        if(description==null || description.isEmpty()){
            model.addAttribute("message", "La descrizione è obbligatoria");
            return "beneficiari/create";
        }
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setBeneficiario(denominazione);
        beneficiario.setDescrizione(description);
        beneficiario.setUser(user);
        Beneficiario b;
        try {
            b = beneficiarioService.save(beneficiario);
        } catch (Exception e) {
            model.addAttribute("message", "Errore nella creazione del beneficiario: " + e.getMessage());
            return "beneficiari/create";
        }
        return "redirect:/" + b.getId() + "/edit" + "?message1= Inserimento effettuato correttamente. Ora inserisci gli altri dati!";
    }

    private boolean verificaDenominazione(String denominazione, String description, Register user, Model model) {
        Beneficiario beneficiario = beneficiarioService.existsByBeneficiarioAndIdUser(denominazione,user);
        if(beneficiario != null) {
            this.valorizzaInput(model,denominazione, description);
            model.addAttribute("message", "Beneficiario già inserito!");
            return false;
        }
        return true;
    }
    private void valorizzaInput(Model model, String denominazione, String description) {
        model.addAttribute("denominazione", denominazione);
        model.addAttribute("description", description);
    }


    @GetMapping(value = "/{id}/detail")
    public String beneficiariDetail(@PathVariable Integer id,
                               @RequestParam(name = "message", required = false) String message,
                               @RequestParam(name = "message1", required = false) String message1,
                               Model model,
                               Principal principal) {
        Beneficiario beneficiario = beneficiarioService.findById(id);
        if (beneficiario == null) {
            return "security/access-denied";// Gestione caso corso non trovato
        }
        boolean isAdmin = false;
        // L'utente loggato
        String loggedUsername = principal.getName();
        Register user = userService.loadRegisterByUsername(loggedUsername);
        if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            beneficiario.setUser(user);// es: "mariorossi"
            isAdmin = true;
        }
        // Verifico se il proprietario  è lo stesso che ha fatto login
        boolean isOwner = (beneficiario.getUser().getUsername().equals(loggedUsername));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("beneficiario", beneficiario);
        model.addAttribute("message", message);
        model.addAttribute("message1", message1);
        return "beneficiari/detail";

    }
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Integer id,
                             @RequestParam(name = "message", required = false) String message,
                             @RequestParam(name = "message1", required = false) String message1,
                             Model model,
                             Principal principal) {
        Beneficiario beneficiario = beneficiarioService.findById(id);
        if (beneficiario == null) {
            // gestisci errore se non trovato
            return "security/access-denied";
        }
        if(beneficiario.getUser() == null) {
            return "security/access-denied";
        }
        else
        {
            String loggedUsername = principal.getName();
            Register user = userService.loadRegisterByUsername(loggedUsername);
            boolean isOwner = (beneficiario.getUser().getUsername().equals(loggedUsername));
            model.addAttribute("isOwner", isOwner);
            model.addAttribute("beneficiarioForm", beneficiario);
            model.addAttribute("iduser",beneficiario.getUser().getId());
            model.addAttribute("message", message);
            model.addAttribute("message1", message1);
            return "beneficiari/edit";
        }
    }
    // POST /courses/{id} -> aggiorna un corso esistente
    @GetMapping(path = "/{idBeneficiario}/update")
    public String updateBeneficiario(@PathVariable("idBeneficiario") Integer idBeneficiario,
                                     @RequestParam("denominazione") String denominazione,
                                     @RequestParam("descrizione") String descrizione,
                                     @RequestParam("email") String email,
                                     @RequestParam("telefono") String telefono,
                                     @RequestParam("sitoWeb") String sitoWeb,
                                     Model model,
                                     Principal principal) {
        //SANITIZZAZIONE
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setBeneficiario(denominazione);
        beneficiario.setEmail(email);
        beneficiario.setTelefono(telefono);
        beneficiario.setSitoWeb(sanitizerService.sanitize(sitoWeb));
        beneficiario.setDescrizione(sanitizerService.sanitize(descrizione));
        // L'utente loggato
        String loggedUsername = principal.getName(); // es: "mariorossi"
        Register user = userService.loadRegisterByUsername(loggedUsername);
        // Verifico se il proprietario  è lo stesso che ha fatto login
        if (!user.getUsername().equals(loggedUsername)) {
                // se non sei il proprietario, redirect o errore
            return "security/access-denied";
        }
        else {
            beneficiario.setUser(user);
        }
        beneficiario.setId(idBeneficiario);
        try{
            beneficiarioService.update(beneficiario);
            String message = validazioni(beneficiario,model);
            if(message != null) {
                model.addAttribute("message", message);
                return "redirect:/" + beneficiario.getId() + "/edit" + "?message=" + message;
            }
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/" + beneficiario.getId() + "/edit";
        }
        return "redirect:/beneficiari/list?message=Beneficiario aggiornato con successo!";
    }

    private String validazioni(Beneficiario update, Model model) {
        String message = null;
        if(update.getBeneficiario() == null || update.getBeneficiario().isEmpty()) {
            message = "Valorizzare il beneficiario";
            return message;
        }
        if(update.getDescrizione() == null || update.getDescrizione().isEmpty()) {
            message = "Valorizzare la descrizione";
            return message;
        }
        if(update.getEmail() == null || update.getEmail().isEmpty()) {
            message = "Valorizzare l'email";
            return message;
        }
        if(update.getTelefono() == null || update.getTelefono().isEmpty()) {
            message = "Valorizzare il telefono";
            return message;
        }
        return message;
    }

    // POST /beneficiari/{id}/delete -> cancella un beneficiario
    @PostMapping("/{id}/delete")
    public String deleteBeneficiario(@PathVariable("id") Integer id,Principal principal,Model model) {
        Beneficiario beneficiario = beneficiarioService.findById(id);
        String denominazione = beneficiario.getBeneficiario();
        String loggedUsername = principal.getName(); // es: "mario rossi"
        // Verifico se il proprietario è lo stesso che ha fatto la login
        boolean isOwner = beneficiario.getUser().getUsername().equals(loggedUsername);
        Register user = userService.loadRegisterByUsername(loggedUsername);
        if (!isOwner) {
            // se non sei il proprietario, redirect o errore
            return "security/access-denied";
        }

        Beneficiario b = scadenzeRepository.findBeneficiarioById(id);
        List<Scadenza> scadenze = b.getScadenze();
        for(Scadenza scadenza : scadenze) {
            Subscription s = subscriptionRepository.findByScadenza(scadenza);
            if (s != null) {
                return "redirect:/beneficiari/list?message1=Il beneficiario che si sta tentando di eliminare contiene una scadenza pagata. Impossibile eliminarlo!";
            }
        }
        beneficiarioService.deleteById(id);
        return "redirect:/" + id + "/" + denominazione + "/infobeneficiario";
    }


    @GetMapping("/{id}/{denominazione}/infobeneficiario")
    public String infoAdmin(@PathVariable("id") Integer id,
                            @PathVariable("denominazione") String denominazione)
    {
        try {
            emailService.sendSimpleEmail(
                    adminConfig.getEmail(),
                    "Eliminazione Beneficiario",
                    "Il beneficiario con identificativo " + id + " e denominazione " + denominazione + " è stato eliminato!"
            );
        } catch (Exception e) {
            return "redirect:/beneficiari/list?message=Errore invio email "+ e.getMessage();
        }
        return "redirect:/beneficiari/list?message=Beneficiario eliminato con successo ed email all'amministratore inviata correttamente!";
    }



















}
