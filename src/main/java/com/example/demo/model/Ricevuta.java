package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Ricevute")
public class Ricevuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "nome_file",nullable = false, length = 450)
    private String nomeFile;

    @Column(name = "tipo_file",nullable = false, length = 45)
    private String tipoFile;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "scadenza_id")
    private Scadenza scadenza;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getTipoFile() {
        return tipoFile;
    }

    public void setTipoFile(String tipoFile) {
        this.tipoFile = tipoFile;
    }

    public Scadenza getScadenza() {
        return scadenza;
    }

    public void setScadenza(Scadenza scadenza) {
        this.scadenza = scadenza;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
