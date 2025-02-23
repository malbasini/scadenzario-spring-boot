package com.example.demo.service;

import com.example.demo.model.Beneficiario;

import java.util.List;
import java.util.Optional;

public interface BeneficiarioService {

    Beneficiario save(Beneficiario beneficiario);
    List<Beneficiario> findAll();
    Beneficiario findById(int id);
    void deleteById(Integer id);
    Beneficiario update(Beneficiario beneficiario);

}
