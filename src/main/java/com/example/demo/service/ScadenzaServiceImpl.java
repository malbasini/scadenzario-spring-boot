package com.example.demo.service;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import com.example.demo.model.Scadenza;
import com.example.demo.repository.ScadenzeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
@Service
public class ScadenzaServiceImpl implements ScadenzaService {

    @Autowired
    private ScadenzeRepository scadenzeRepository;

    @Override
    public Scadenza save(Scadenza scadenza) {
        return scadenzeRepository.save(scadenza);
    }
    @Override
    public List<Scadenza> findAll() {
        return scadenzeRepository.findAll();
    }
    @Override
    public Scadenza findById(Integer id) {
        return scadenzeRepository.findById(id).orElseThrow(() -> new RuntimeException("Scadenza not found with ID: " + id));
    }
    @Override
    public void deleteById(Integer id) {
        scadenzeRepository.deleteById(id);
    }
    @Override
    public Scadenza update(Scadenza scadenza) {
        return scadenzeRepository.save(scadenza);
    }
    @Transactional
    public Page<Scadenza> findScadenze(int page, int size, String beneficiario, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy);
        sort = sortDirection.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        // Filtro per beneficiario e data scadenza
        if (beneficiario != null && !beneficiario.isEmpty()) {
            //PARSING DELLA DATA SE L'UTENTE RICERCA PER DATA
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate data = null;
            // Controllare se l'utente ha fornito una data valida
            try {
                data = LocalDate.parse(beneficiario, formatter);
                return scadenzeRepository.findByDataScadenza(data, pageable);
            } catch (DateTimeParseException e) {
                //SE LA DATA NON è VALIDA RICERCO PER BENEFICIARIO
                Page<Scadenza> scadenze = scadenzeRepository.findByDenominazioneContainingIgnoreCase(beneficiario, pageable);
                return scadenze;
            }
        }
        else
            return scadenzeRepository.findAll(pageable);
    }
    @Transactional
    public Beneficiario findByBeneficiarioAndIdUser(String beneficiario, Register user){
        Beneficiario b = scadenzeRepository.findByBeneficiarioAndIdUser(beneficiario,user.getId());
        return b;
    }
    @Transactional
    public List<Beneficiario> findBeneficiariByIdUser(Integer id){
        return scadenzeRepository.findBeneficiariByIdUser(id);
    }

    @Transactional
    public Beneficiario findByIdScadenza(Integer id){
        Scadenza scadenza = scadenzeRepository.findById(id).orElseThrow(() -> new RuntimeException("Scadenza not found with ID: " + id));
        return scadenza.getBeneficiario();
    }



}
