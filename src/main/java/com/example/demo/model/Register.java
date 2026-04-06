package com.example.demo.model;

import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "register")
public class Register {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="username", unique = true, length = 100)
    private String username;
    @Column(name="fullname", length = 255)
    private String fullname;
    @Column(name="password", length = 100)
    private String password;
    @Column(name="email", unique = true, length = 100)
    private String email;
    private boolean enabled;

    @OneToMany(mappedBy = "beneficiario")
    private List<Beneficiario> beneficiari;

    @ManyToMany
    @JoinTable(
            name = "register_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Ruolo> roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Beneficiario> getBeneficiari() {
        return beneficiari;
    }

    public void setBeneficiari(List<Beneficiario> beneficiari) {
        this.beneficiari = beneficiari;
    }

    public List<Ruolo> getRoles() {
        return roles;
    }

    public void setRoles(List<Ruolo> roles) {
        this.roles = roles;
    }
}
