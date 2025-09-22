package com.example.demo.service;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import com.example.demo.model.Scadenza;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ScadenzaService {

    Scadenza update(Scadenza scadenza);
    Scadenza findById(Integer id);
    void deleteById(Integer id);
    Scadenza save(Scadenza scadenza);
    Beneficiario findByBeneficiarioAndIdUser(String beneficiario, Register user);
    List<Beneficiario> findBeneficiariByIdUser(Integer id);
}
