package com.example.demo.service;

import com.example.demo.model.Register;
import com.example.demo.model.Ruolo;

import java.util.List;

public interface UserService {

    void registerNewUser(Register user);
    List<Ruolo> getAllRole();
    Register login(String userName);
    Register loadRegisterByUsername(String username);
}
