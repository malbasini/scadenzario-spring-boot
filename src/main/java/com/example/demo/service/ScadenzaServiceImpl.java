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

    public Page<Scadenza> findScadenze(int page, int size, String beneficiario, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy);
        sort = sortDirection.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        // Filtro per titolo
        if (beneficiario != null && !beneficiario.isEmpty()) {
            return scadenzeRepository.findByBeneficiarioContainsIgnoreCase(beneficiario, pageable);
        }
        return scadenzeRepository.findAll(pageable);
    }

    public Beneficiario findByBeneficiarioAndIdUser(String beneficiario, Register user){
        Beneficiario b = scadenzeRepository.findByBeneficiarioAndIdUser(beneficiario,user.getId());
        return b;
    }

    public List<Beneficiario> findBeneficiariByIdUser(Integer id){
        return scadenzeRepository.findBeneficiariByIdUser(id);
    }

    @Transactional
    public Beneficiario findByIdScadenza(Integer id){
        Scadenza scadenza = scadenzeRepository.findById(id).orElseThrow(() -> new RuntimeException("Scadenza not found with ID: " + id));
        return scadenza.getBeneficiario();
    }



}
