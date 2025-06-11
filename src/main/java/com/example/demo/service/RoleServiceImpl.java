package com.example.demo.service;

import com.example.demo.model.Ruolo;
import com.example.demo.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RolesRepository rolesRepository;


    public Ruolo getRuoloByName(String name){
        return rolesRepository.findByName(name);
    }
}
