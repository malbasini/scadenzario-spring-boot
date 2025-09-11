package com.example.demo.service;

import com.example.demo.dto.CategoriaTotaleDTO;
import com.example.demo.repository.CategoriaTotaleView;
import com.example.demo.repository.ScadenzeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnaliticheSpeseService {

    private final ScadenzeRepository scadenzaRepository;

    public AnaliticheSpeseService(ScadenzeRepository scadenzaRepository) {
        this.scadenzaRepository = scadenzaRepository;
    }

    public List<CategoriaTotaleDTO> getTotaliPerCategoria(LocalDate dal, LocalDate al) {
        List<CategoriaTotaleView> rows = (dal == null && al == null)
                ? scadenzaRepository.sumImportoByCategoria()
                : scadenzaRepository.sumImportoByCategoriaBetween(dal, al);

        return rows.stream()
                .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                .toList();
    }
}