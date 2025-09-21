package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BeneficiariRepository extends JpaRepository<Beneficiario, Integer> {

    Page<Beneficiario> findByBeneficiarioContainsIgnoreCase(String denominazione, Pageable pageable);
    @Query("SELECT b FROM Beneficiario b WHERE b.beneficiario = :denominazione AND b.user.id=:id")
    Beneficiario findByBeneficiarioAndIdUser(@Param("denominazione") String denominazione, @Param("id") Integer id);
}
