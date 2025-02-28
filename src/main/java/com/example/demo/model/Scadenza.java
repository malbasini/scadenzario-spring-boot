package com.example.demo.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "Scadenze")
public class Scadenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "data_scadenza", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataScadenza;

    @Column(name = "importo", nullable = false)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal importo;

    @Column(name = "sollecito")
    private Boolean sollecito;

    @Column(name = "giorni_ritardo")
    private int giorniRitardo;

    @Column(name = "data_pagamento", nullable = true)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
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

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
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

    public long differanzaGiorni() {
        // Controlla se una delle due date è null
        long differenzaGiorni = 0;
        if (dataScadenza == null || dataPagamento == null) {
            System.out.println("Una delle due date è null.");
        } else {
            // Calcolo della differenza
            return differenzaGiorni = ChronoUnit.DAYS.between(dataScadenza, dataPagamento);
        }
        return 0;
    }
}
