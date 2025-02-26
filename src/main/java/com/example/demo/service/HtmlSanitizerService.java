package com.example.demo.service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;


@Service
public class HtmlSanitizerService {

    private final PolicyFactory policyFactory;

    // Possiamo costruire la policy in un costruttore o in un metodo statico
    public HtmlSanitizerService() {
        PolicyFactory policy = new HtmlPolicyBuilder()
                .allowCommonBlockElements()
                .allowCommonInlineFormattingElements()
                .allowElements("a")
                .allowAttributes("href").onElements("a")
                .allowStandardUrlProtocols()
                .allowElements("img")
                .allowAttributes("src").onElements("img")
                .allowUrlProtocols("data")
                .toFactory();

        // Combiniamo con Sanitizers predefiniti
        this.policyFactory = Sanitizers.FORMATTING.and(policy);
    }

    public String sanitize(String inputHtml) {
        // Se inputHtml è null o vuoto, gestiscilo di conseguenza
        if (inputHtml == null || inputHtml.isEmpty()) {
            return inputHtml;
        }
        return policyFactory.sanitize(inputHtml);
    }
}