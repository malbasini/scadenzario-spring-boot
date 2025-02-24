package com.example.demo.controller;


import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import com.example.demo.service.BeneficiarioService;
import com.example.demo.service.CaptchaValidator;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
public class BeneficiarioController {

    @Autowired
    private CaptchaValidator captchaValidator;

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private UserService userService;


    // GET /courses -> listing di tutti i corsi con supporto a paginazione, ricerca e ordinamento
    @GetMapping("/beneficiari/list")
    public String listBeneficiari(
            @RequestParam(defaultValue = "0") int page, // Pagina corrente
            @RequestParam(defaultValue = "1") int size, // Elementi per pagina
            @RequestParam(defaultValue = "") String beneficiario, // Filtro per beneficiario
            @RequestParam(defaultValue = "beneficiario") String sortBy, // Campo di ordinamento
            @RequestParam(defaultValue = "asc") String sortDirection,
            Principal principal,
            Model model) {

        String loggedUsername = principal.getName();
        Register user = userService.loadRegisterByUsername(loggedUsername);
        // Ottenere i corsi con paginazione, ricerca e ordinamento
        Page<Beneficiario> beneficiari = beneficiarioService.findBeneficiari(page, size, beneficiario, sortBy, sortDirection);

        // Passare i dati al modello per JSP
        model.addAttribute("beneficiari", beneficiari.getContent().stream()
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList()));
        // Lista dei corsi
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", beneficiari.getTotalPages());
        model.addAttribute("titleFilter", beneficiario);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);

        return "/beneficiari/list"; // Nome della vista JSP
    }
    @GetMapping("/beneficiario/new")
    public String showCreateForm(Model model) {
        Beneficiario beneficiario = new Beneficiario();
        model.addAttribute("beneficiario", beneficiario);
        return "/beneficiari/create";
    }
    @PostMapping("/beneficiario/create")
    public String create(@RequestParam("g-recaptcha-response") String captchaResponse,
                         @RequestParam String denominazione,
                         @RequestParam String description,
                         Principal principal,
                         Model model) {
        String username = principal.getName();  // lo username loggato
        Register user = userService.loadRegisterByUsername(username);
        if(!verificaDenominazione(denominazione,user,model)) {
            this.valorizzaInput(model,denominazione,description);
            return "/beneficiari/create";
        }
        this.valorizzaInput(model,denominazione,description);
        boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            model.addAttribute("message", "Captcha non valido. Riprova.");
            return "/beneficiari/create";// Torna alla pagina del form
        }
        if(denominazione==null || denominazione.isEmpty()){
            model.addAttribute("message", "Il beneficiario è obbligatorio");
            return "/beneficiari/create";
        }
        if(description==null || description.isEmpty()){
            model.addAttribute("message", "La descrizione è obbligatoria");
            return "/beneficiari/create";
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
            return "/beneficiari/create";
        }
        return "redirect:/" + b.getId() + "/edit" + "?message1= Inserimento effettuato correttamente. Ora inserisci gli altri dati!";
    }

    private boolean verificaDenominazione(String denominazione, Register user, Model model) {
        Beneficiario beneficiario = beneficiarioService.existsByBeneficiarioAndIdUser(denominazione,user);
        if(beneficiario != null) {
            model.addAttribute("message", "Beneficiario già inserito!");
            return false;
        }
        return true;
    }
    private void valorizzaInput(Model model, String denominazione, String description) {
        model.addAttribute("denominazione", denominazione);
        model.addAttribute("description", description);
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
            boolean isAdmin = false;
            // L'utente loggato
            String loggedUsername = principal.getName();
            Register user = userService.loadRegisterByUsername(loggedUsername);
            if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
                isAdmin = true;
                model.addAttribute("isAdmin",isAdmin);
            }
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("beneficiarioForm", beneficiario);
            model.addAttribute("iduser",beneficiario.getUser().getId());
            model.addAttribute("message", message);
            model.addAttribute("message1", message1);
            return "/beneficiari/edit";
        }
    }
    // POST /courses/{id} -> aggiorna un corso esistente
    @PostMapping("/{idBeneficiario}/update")
    public String updateBeneficiario(@PathVariable("idBeneficiario") Integer idBeneficiario,
                                     @ModelAttribute("beneficiarioForm") Beneficiario beneficiario,
                                     Model model,
                                     Principal principal) {
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
        return "redirect:/" + beneficiario.getId() + "/edit?message1=Beneficiario aggiornato con successo!";
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
        return message;
    }

    // POST /beneficiari/{id}/delete -> cancella un beneficiario
    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable("id") Integer id,Principal principal,Model model) {
        Beneficiario beneficiario = beneficiarioService.findById(id);
        String loggedUsername = principal.getName(); // es: "mario rossi"
        // Verifico se il proprietario è lo stesso che ha fatto la login
        boolean isOwner = beneficiario.getUser().getUsername().equals(loggedUsername);
        Register user = userService.loadRegisterByUsername(loggedUsername);
        if (!isOwner) {
            // se non sei il proprietario, redirect o errore
            return "redirect:security/access-denied";
        }
        beneficiarioService.deleteById(id);
        return "redirect:/beneficiari/list?message1=Course deleted successfully";
    }



















}
