package com.example.demo.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "beneficiari")
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "beneficiario",nullable = false, length = 450)
    private String beneficiario;

    @Column(name = "descrizione",nullable = false, length = 3000)
    private String descrizione;

    @Column(name = "email",nullable = true, length = 150)
    private String email;

    @Column(name = "telefono",nullable = true, length = 50)
    private String telefono;

    @Column(name = "sito_web",nullable = true, length = 150)
    private String sitoWeb;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Register user;

    @OneToMany(mappedBy = "beneficiario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)

    private List<Scadenza> scadenze;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        id = id;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSitoWeb() {
        return sitoWeb;
    }

    public void setSitoWeb(String sitoWeb) {
        this.sitoWeb = sitoWeb;
    }

    public Register getUser() {
        return user;
    }

    public void setUser(Register user) {
        this.user = user;
    }

    public List<Scadenza> getScadenze() {
        return scadenze;
    }

    public void setScadenze(List<Scadenza> scadenze) {
        this.scadenze = scadenze;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
