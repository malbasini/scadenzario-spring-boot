package com.example.demo.service;

import com.example.demo.model.Ruolo;
import com.example.demo.model.Register;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService{
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RolesRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public void registerNewUser(Register user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    public List<Ruolo> getAllRole()
    {
        return roleRepository.findAll();
    }
    public Register login(String userName) {
        Register user = userRepository.findByUsername(userName);
        if (user == null)
            throw new IllegalArgumentException("Invalid User Name");
        return user;
    }
    public Register loadRegisterByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
    }
}
