package com.example.demo.service;

import com.example.demo.dto.CategoriaTotaleDTO;
import com.example.demo.repository.CategoriaTotaleView;
import com.example.demo.repository.ScadenzeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class AnaliticheSpeseService {

    private final ScadenzeRepository scadenzaRepository;

    public AnaliticheSpeseService(ScadenzeRepository scadenzaRepository) {
        this.scadenzaRepository = scadenzaRepository;
    }

    public List<CategoriaTotaleDTO> getTotaliPerCategoria(LocalDate dal, LocalDate al, Object filter) {

        if (filter == "" || filter == null)
            filter = null;
        else if (filter.toString().length()==10)
        {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(filter.toString(), fmt);
            filter = date;
        }
        LocalDate dataScadenza = null;
        String beneficiario = null;
        if (filter instanceof LocalDate data) {
            // filter è una data
            dataScadenza = data;
        } else if (filter instanceof String testo) {
            // filter è una stringa
            beneficiario = testo;
        }

        if (Objects.nonNull(dataScadenza)) {
            List<CategoriaTotaleView> rows = (dal == null && al == null)
                    ? scadenzaRepository.sumImportoByCategoriaAndScadenza(dataScadenza)
                    : scadenzaRepository.sumImportoByCategoriaAndScadenzaBetween(dal, al, dataScadenza);

            return rows.stream()
                    .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                    .toList();
        } else if (Objects.nonNull(beneficiario)) {
            List<CategoriaTotaleView> rows = (dal == null && al == null)
                    ? scadenzaRepository.sumImportoByCategoriaAndBeneficiario(beneficiario)
                    : scadenzaRepository.sumImportoByCategoriaAndBeneficiarioBetween(dal, al, beneficiario);

            return rows.stream()
                    .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                    .toList();
        }
        else{
            List<CategoriaTotaleView> rows = (dal == null && al == null)
                    ? scadenzaRepository.sumImportoByCategoria()
                    : scadenzaRepository.sumImportoByCategoriaBetween(dal, al);

            return rows.stream()
                    .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                    .toList();
        }

    }
}