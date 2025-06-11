package com.example.demo.service;

import com.example.demo.model.Ricevuta;

import java.util.List;

public interface RicevutaService {

    Ricevuta save(Ricevuta ricevuta);
    List<Ricevuta> findAll();
    Ricevuta findById(Integer id);
    void deleteById(Integer id);
    List<Ricevuta> findRicevuteByIdScadenza(int idScadenza);
}
