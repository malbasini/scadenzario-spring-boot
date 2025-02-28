package com.example.demo.controller;

import com.example.demo.model.Register;
import com.example.demo.model.Ruolo;
import com.example.demo.repository.RolesRepository;
import com.example.demo.service.CaptchaValidator;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;

@Controller
public class AuthController {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private RolesRepository roleRepository;
    @Autowired
    private CaptchaValidator captchaValidator;

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage()
    {
        return "redirect:/login?logout=true"; // restituisce la JSP login.html
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model)
    {
        Register userForm = new Register();
        model.addAttribute("userForm",userForm);
        return "security/login"; // restituisce la JSP login.html
    }
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    public String doLogin(@ModelAttribute("userForm") Register userForm,
                          Model model) {
        model.addAttribute("username",userForm.getUsername());
        boolean isAuthenticated;
        Register user = null;
        try {
            user = userService.loadRegisterByUsername(userForm.getUsername());
        } catch (Exception e) {
            model.addAttribute("errore","I dati inseriti non sono corretti");
            return "security/login"; // restituisce la JSP login.html
        }
        if(user.getUsername().equals(userForm.getUsername()) && passwordEncoder.matches(userForm.getPassword(), user.getPassword()))
        {
            isAuthenticated = true;
            model.addAttribute("username",userForm.getUsername());
            model.addAttribute("isAuthenticated",isAuthenticated);
            return "redirect:/";
        }
        model.addAttribute("errore","I dati inseriti non sono corretti");
        return "security/login"; // restituisce la JSP login.html
    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterPage(Model model,
                                  HttpServletRequest request) {

        model.addAttribute("contextPath", request.getContextPath());
        return "security/register"; // restituisce la JSP register.html
    }
    @RequestMapping(value = "/doRegister", method = RequestMethod.POST)
    public String doRegister(
            @RequestParam("username") String username,
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("roleId") String role,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            Model model) {
            this.valorizzaCampi(model, username, fullname, email, password, role);
            //Control Role ADMIN
            try {
                Register user = userService.loadRegisterByUsername(username);
                if(user.getRoles().stream().anyMatch(role1 -> role1.getName().equals("ADMIN"))) {
                    if(role.equals("ADMIN".toUpperCase(Locale.ROOT))) {
                        model.addAttribute("errore", "Ruolo ADMIN già inserito");
                        return "security/register";
                    }
                }
            }catch (Exception e) {
            }
            if (fullname.isEmpty()) {
                model.addAttribute("errore", "Valorizzare il fullname");
                return "security/register";
            }
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                model.addAttribute("errore", "Valorizzare username, password ed email");
                return "security/register";
            }
            if (!role.equals("ADMIN") && (!role.equals("USER"))) {
                model.addAttribute("errore", "Valorizzare il ruolo");
                return "security/register";
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                model.addAttribute("errore", "Email non valida");
                return "security/register";
            }
            if (password.length() < 5) {
                model.addAttribute("errore", "Password non valida. La lunghezza deve essere di almeno cinque caratteri");
                return "security/register";
            }
            boolean isCaptchaValid = captchaValidator.verifyCaptcha(captchaResponse);
            if (!isCaptchaValid) {
                model.addAttribute("error", "Captcha non valido. Riprova.");
                this.valorizzaCampi(model, username, fullname, email, password, role);
                return "security/register";// Torna alla pagina del form
            }
            Register user1 = new Register();
            user1.setFullname(fullname);
            user1.setEmail(email);
            user1.setPassword(password);
            user1.setUsername(username);
            user1.setEnabled(true);
            Ruolo ruolo1 = roleRepository.findByName(role);
            user1.setRoles(java.util.List.of(ruolo1));
            // Qui invochiamo il servizio che crea l'utente e assegna il ruolo
            try {
                userService.registerNewUser(user1);
                model.addAttribute("message", "Registrazione effettuata con successo. Ora fai il login!");
                return "security/register";
            } catch (Exception e) {
                model.addAttribute("errore", "Errore nella registrazione: " + e.getMessage());
                return "security/register";
            }
        }

    private void valorizzaCampi(Model model, String username, String fullname, String email, String password, String role) {
        model.addAttribute("username", username);
        model.addAttribute("fullname", fullname);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("role", role);
    }
}
