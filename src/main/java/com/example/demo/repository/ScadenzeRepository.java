package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import com.example.demo.model.Scadenza;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScadenzeRepository extends JpaRepository<Scadenza,Integer> {

    @Transactional
    @Query("SELECT s FROM Scadenza s WHERE s.beneficiario = :denominazione")
    Scadenza findByBeneficiario(@Param("denominazione") String denominazione);
    Page<Scadenza> findByBeneficiarioContainsIgnoreCase(String denominazione, Pageable pageable);

    @Transactional
    @Query("SELECT b FROM Beneficiario b WHERE b.beneficiario = :denominazione AND b.user.id=:id")
    Beneficiario findByBeneficiarioAndIdUser(@Param("denominazione") String denominazione, @Param("id") Integer id);

    @Transactional
    @Query("SELECT b FROM Beneficiario b WHERE b.user.id=:id")
    List<Beneficiario> findBeneficiariByIdUser(@Param("id") Integer id);

    @Query("SELECT b FROM Beneficiario b WHERE b.Id = :id")
    Beneficiario findBeneficiarioById(@Param("id") Integer id);
}
