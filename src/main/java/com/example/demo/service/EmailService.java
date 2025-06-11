package com.example.demo.service;

import com.example.demo.config.AdminConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private AdminConfig adminConfig;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom("no-reply@localhost");
            message.setTo(adminConfig.getEmail());
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            String errorMessage = "Errore nell'invio dell'email: " + e.getMessage();
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}