package com.example.demo.repository;

import com.example.demo.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Ruolo,Integer> {
    Ruolo findByName(String name);


}
