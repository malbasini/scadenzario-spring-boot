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
@Transactional
public class ScadenzaServiceImpl implements ScadenzaService {


    private final ScadenzeRepository scadenzeRepository;

    public ScadenzaServiceImpl(ScadenzeRepository scadenzeRepository) {
        this.scadenzeRepository = scadenzeRepository;
    }

    @Override
    public Scadenza save(Scadenza scadenza) {
        return scadenzeRepository.save(scadenza);
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

    public Beneficiario findByBeneficiarioAndIdUser(String beneficiario, Register user){
        Beneficiario b = scadenzeRepository.findByBeneficiarioAndIdUser(beneficiario,user.getId());
        return b;
    }

    public List<Beneficiario> findBeneficiariByIdUser(Integer id){
        return scadenzeRepository.findBeneficiariByIdUser(id);
    }

}
