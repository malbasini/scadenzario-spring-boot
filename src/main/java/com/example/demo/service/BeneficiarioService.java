package com.example.demo.service;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Register;
import org.springframework.data.domain.Page;
import java.util.List;

public interface BeneficiarioService {

    Beneficiario save(Beneficiario beneficiario);
    Beneficiario findById(int id);
    void deleteById(Integer id);
    Beneficiario update(Beneficiario beneficiario);
    Page<Beneficiario> findBeneficiari(int page, int size, String denominazione, String sortBy, String sortDirection);
    Beneficiario existsByBeneficiarioAndIdUser(String beneficiario, Register user);
}
