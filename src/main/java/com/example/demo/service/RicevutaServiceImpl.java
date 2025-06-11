package com.example.demo.service;

import com.example.demo.model.Ricevuta;
import com.example.demo.repository.RicevuteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RicevutaServiceImpl implements RicevutaService {

    @Autowired
    private RicevuteRepository ricevuteRepository;


    public Ricevuta save(Ricevuta ricevuta) {
        return ricevuteRepository.save(ricevuta);
    }
    public List<Ricevuta> findAll() {
        return ricevuteRepository.findAll();
    }
    public Ricevuta findById(Integer id) {
        return ricevuteRepository.findById(id).orElseThrow();
    }
    public void deleteById(Integer id) {
        ricevuteRepository.deleteById(id);
    }
    public List<Ricevuta> findRicevuteByIdScadenza(int idScadenza) {
        return ricevuteRepository.findRicevutaByIdScadenza(idScadenza);
    }
}
