package com.example.demo.service;

import com.example.demo.model.Scadenza;

import java.util.List;

public interface ScadenzaService {

    Scadenza update(Scadenza scadenza);
    List<Scadenza> findAll();
    Scadenza findById(Integer id);
    void deleteById(Integer id);
    Scadenza save(Scadenza scadenza);
}
