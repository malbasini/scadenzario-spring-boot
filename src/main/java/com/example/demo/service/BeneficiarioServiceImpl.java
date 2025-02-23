package com.example.demo.service;

import com.example.demo.model.Beneficiario;
import com.example.demo.repository.BeneficiariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BeneficiarioServiceImpl implements BeneficiarioService {
    @Autowired
    private BeneficiariRepository beneficiariRepository;

    @Override
    public Beneficiario save(Beneficiario beneficiario) {
        return beneficiariRepository.save(beneficiario);
    }
    @Override
    public List<Beneficiario> findAll() {
        return beneficiariRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        beneficiariRepository.deleteById(id);
    }
    @Override
    public Beneficiario findById(int id) {
        return beneficiariRepository.findById(id).orElseThrow();
    }
    @Override
    public Beneficiario update(Beneficiario beneficiario) {
        return beneficiariRepository.save(beneficiario);
    }

}
