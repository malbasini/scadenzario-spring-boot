package com.example.demo.service;

import com.example.demo.model.Register;

public interface UserService {

    void registerNewUser(Register user);
    Register loadRegisterByUsername(String username);
}
