package com.example.demo.repository;

import com.example.demo.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiariRepository extends JpaRepository<Beneficiario, Integer> {
}
