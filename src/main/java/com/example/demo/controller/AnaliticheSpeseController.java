package com.example.demo.controller;

import com.example.demo.dto.CategoriaTotaleDTO;
import com.example.demo.service.AnaliticheSpeseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Controller
@RequestMapping
public class AnaliticheSpeseController {

    private final AnaliticheSpeseService analiticheSpeseService;

    public AnaliticheSpeseController(AnaliticheSpeseService analiticheSpeseService) {
        this.analiticheSpeseService = analiticheSpeseService;
    }
    // Pagina HTML con grafico (Thymeleaf: templates/scadenze/grafico-categorie.html)
    @GetMapping({"/scadenze/grafico-categorie"})
    public String graficoCategorie(
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) LocalDate dal,
            @RequestParam(required = false) LocalDate al,
            @RequestParam(name = "filter", required = false) Object filter,
            Model model
    ) {
        int annoSelezionato = (anno == null ? Year.now().getValue() : anno);
        List<CategoriaTotaleDTO> data = analiticheSpeseService.getTotaliPerCategoria(dal,al,filter,annoSelezionato);

        // separo già labels e values per semplicità nel template
        List<String> labels = data.stream().map(CategoriaTotaleDTO::categoria).toList();
        List<BigDecimal> values = data.stream().map(CategoriaTotaleDTO::totale).toList();

        // per combo in pagina grafico
        model.addAttribute("anni", analiticheSpeseService.getAnniDisponibili());
        model.addAttribute("annoSelezionato", annoSelezionato);
        model.addAttribute("labels", labels);
        model.addAttribute("values", values);
        model.addAttribute("dal", dal);
        model.addAttribute("al", al);
        model.addAttribute("filter", filter);


        return "scadenze/grafico-categorie";
    }
}