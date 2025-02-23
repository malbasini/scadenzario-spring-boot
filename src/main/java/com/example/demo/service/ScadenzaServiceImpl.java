package com.example.demo.service;

import com.example.demo.model.Scadenza;
import com.example.demo.repository.ScadenzeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return scadenzeRepository.findById(id).orElseThrow();
    }
    @Override
    public void deleteById(Integer id) {
        scadenzeRepository.deleteById(id);
    }
    @Override
    public Scadenza update(Scadenza scadenza) {
        return scadenzeRepository.save(scadenza);
    }
}
