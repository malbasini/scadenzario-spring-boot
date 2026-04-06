package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Ricevuta;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RicevuteRepository extends JpaRepository<Ricevuta, Integer>
{
    @Query("SELECT b FROM Ricevuta b WHERE b.scadenza.Id=:id")
    List<Ricevuta> findRicevutaByIdScadenza(@Param("id") Integer id);
}
