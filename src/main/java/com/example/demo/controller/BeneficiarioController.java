package com.example.demo.controller;


import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import com.example.demo.service.BeneficiarioService;
import com.example.demo.service.CaptchaValidator;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
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

        return "beneficiari/list"; // Nome della vista JSP
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
        try {
            beneficiarioService.save(beneficiario);
        } catch (Exception e) {
            model.addAttribute("message", "Errore nella creazione del beneficiario: " + e.getMessage());
            return "beneficiari/create";
        }
        return "/beneficiari/edit";
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
}
