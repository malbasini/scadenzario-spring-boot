package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "Scadenze")
public class Scadenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "data_scadenza", nullable = false)
    private LocalDate dataScadenza;

    @Column(name = "importo", nullable = false)
    private BigDecimal importo;

    @Column(name = "sollecito")
    private Boolean sollecito;

    @Column(name = "giorni_ritardo")
    private int giorniRitardo;

    @Column(name = "data_pagamento", nullable = true)
    private LocalDate dataPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id")
    private Beneficiario beneficiario;

    @OneToMany(mappedBy = "scadenza", cascade = CascadeType.ALL)
    private List<Ricevuta> ricevute;

    @Column(name = "denominazione", nullable = false)
    private String denominazione;

    @Column(name = "status", nullable = true)
    private String status;

    @Transient
    private String importoFormattato;
    @Transient
    private String dataScadenzaFormattata;
    @Transient
    private String dataPagamentoFormattata;


    public long differanzaGiorni() {
        // Controlla se una delle due date è null
        long differenzaGiorni = 0;
        if (getDataScadenza() == null || getDataPagamento() == null) {
            System.out.println("Una delle due date è null.");
        } else {
            // Calcolo della differenza
            return differenzaGiorni = ChronoUnit.DAYS.between(getDataScadenza(), getDataPagamento());
        }
        return 0;
    }


    public String getImportoFormattato() {
        return importoFormattato;
    }
    public void setImportoFormattato(BigDecimal importo) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        String formattedImporto = currencyFormatter.format(importo);
        this.importoFormattato = formattedImporto;
    }

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

    public String getDataScadenzaFormattata() {
        return dataScadenzaFormattata;
    }
    public void setDataScadenzaFormattata(LocalDate dataScadenzaFormattata) {
        String formattedDate = getDataScadenza().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.dataScadenzaFormattata = formattedDate;
    }

    public String getDataPagamentoFormattata() {
        return dataPagamentoFormattata;
    }

    public void setDataPagamentoFormattata(LocalDate dataPagamentoFormattata) {
        String formattedDate = null;
        if(dataPagamentoFormattata != null)
            formattedDate = getDataPagamento().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.dataPagamentoFormattata = formattedDate;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
