package com.example.demo.service;

import com.example.demo.dto.CategoriaTotaleDTO;
import com.example.demo.model.Scadenza;
import com.example.demo.repository.CategoriaTotaleView;
import com.example.demo.repository.ScadenzeRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AnaliticheSpeseService {

    private final ScadenzeRepository scadenzaRepository;

    public AnaliticheSpeseService(ScadenzeRepository scadenzaRepository) {
        this.scadenzaRepository = scadenzaRepository;
    }

    public List<CategoriaTotaleDTO> getTotaliPerCategoria(LocalDate dal, LocalDate al, Object filter, int anno) {

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
            beneficiario = "%"+beneficiario+"%";
        }

        if (Objects.nonNull(dataScadenza)) {
            List<CategoriaTotaleView> rows = (dal == null && al == null)
                    ? scadenzaRepository.sumImportoByCategoriaAndScadenza(dataScadenza,anno)
                    : scadenzaRepository.sumImportoByCategoriaAndScadenzaBetween(dal, al, dataScadenza,anno);

            return rows.stream()
                    .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                    .toList();
        } else if (Objects.nonNull(beneficiario)) {
            List<CategoriaTotaleView> rows = (dal == null && al == null)
                    ? scadenzaRepository.sumImportoByCategoriaAndBeneficiario(beneficiario,anno)
                    : scadenzaRepository.sumImportoByCategoriaAndBeneficiarioBetween(dal, al, beneficiario,anno);

            return rows.stream()
                    .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                    .toList();
        }
        else{
            List<CategoriaTotaleView> rows = (dal == null && al == null)
                    ? scadenzaRepository.sumImportoByCategoria(anno)
                    : scadenzaRepository.sumImportoByCategoriaBetween(dal, al,anno);

            return rows.stream()
                    .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                    .toList();
        }

    }
    public List<Integer> getAnniDisponibili() {
        List<Integer> anni = scadenzaRepository.findDistinctYears();
        // Se il DB non ha ancora l'anno corrente, lo aggiungo in testa.
        int current = Year.now().getValue();
        if (anni.stream().noneMatch(y -> y != null && y == current)) {
            anni.addFirst(current);
        }
        return anni;
    }

    public Page<Scadenza> getScadenzePageable(int page, int size, String beneficiario, String sortBy, String sortDirection, Integer anno) {
        Sort sort = Sort.by(sortBy);
        sort = sortDirection.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        int target = (anno == null ? Year.now().getValue() : anno);
        // Filtro per beneficiario e data scadenza
        if (beneficiario != null && !beneficiario.isEmpty()) {
            //PARSING DELLA DATA SE L'UTENTE RICERCA PER DATA
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate data = null;
            // Controllare se l'utente ha fornito una data valida
            try {
                data = LocalDate.parse(beneficiario, formatter);
                return scadenzaRepository.findAllByYearAndData(target, data ,pageable);
            } catch (DateTimeParseException e) {
                //SE LA DATA NON è VALIDA RICERCO PER BENEFICIARIO
                return scadenzaRepository.findAllByYearAndBeneficiario(target, beneficiario ,pageable);
            }
        }
        else
            return scadenzaRepository.findAllByYear(target,pageable);
    }

    public List<CategoriaTotaleDTO> getTotaliPerCategoriaAnno(Integer anno) {
        int target = (anno == null ? Year.now().getValue() : anno);
        List<CategoriaTotaleView> rows = scadenzaRepository.sumImportoByCategoriaForYear(target);
        return rows.stream()
                .map(r -> new CategoriaTotaleDTO(r.getCategoria(), r.getTotale()))
                .toList();
    }
}