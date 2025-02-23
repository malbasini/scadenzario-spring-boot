package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Scadenze")
public class Scadenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "data_scadenza", nullable = false)
    private Date dataScadenza;

    @Column(name = "importo", nullable = false)
    private BigDecimal importo;

    @Column(name = "sollecito")
    private Boolean sollecito;

    @Column(name = "giorni_ritardo")
    private int giorniRitardo;

    @Column(name = "data_pagamento", nullable = false)
    private Date dataPagamento;

    @ManyToOne
    @JoinColumn(name = "beneficiario_id")
    private Beneficiario beneficiario;

    @OneToMany(mappedBy = "scadenza", cascade = CascadeType.ALL)
    private List<Ricevuta> ricevute;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Date getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public Boolean getSollecito() {
        return sollecito;
    }

    public void setSollecito(Boolean sollecito) {
        this.sollecito = sollecito;
    }

    public int getGiorniRitardo() {
        return giorniRitardo;
    }

    public void setGiorniRitardo(int giorniRitardo) {
        this.giorniRitardo = giorniRitardo;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    public List<Ricevuta> getRicevute() {
        return ricevute;
    }

    public void setRicevute(List<Ricevuta> ricevute) {
        this.ricevute = ricevute;
    }
}
