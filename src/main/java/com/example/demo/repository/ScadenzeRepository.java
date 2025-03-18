package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Scadenza;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScadenzeRepository extends JpaRepository<Scadenza,Integer> {

    Page<Scadenza> findByBeneficiarioContainsIgnoreCase(String denominazione, Pageable pageable);

    @Transactional
    @Query("SELECT b FROM Beneficiario b WHERE b.beneficiario = :denominazione AND b.user.id=:id")
    Beneficiario findByBeneficiarioAndIdUser(@Param("denominazione") String denominazione, @Param("id") Integer id);

    @Transactional
    @Query("SELECT b FROM Beneficiario b WHERE b.user.id=:id")
    List<Beneficiario> findBeneficiariByIdUser(@Param("id") Integer id);

    @Query("SELECT b FROM Beneficiario b WHERE b.Id = :id")
    Beneficiario findBeneficiarioById(@Param("id") Integer id);

    Page<Scadenza> findByDataScadenzaContainsIgnoreCase(LocalDate data, Pageable pageable);

    Page<Scadenza> findByDataScadenza(LocalDate data, Pageable pageable);

    Page<Scadenza> findByDenominazione(String beneficiario, Pageable pageable);

    Page<Scadenza> findByDenominazioneContainingIgnoreCase(String beneficiario, Pageable pageable);
}
