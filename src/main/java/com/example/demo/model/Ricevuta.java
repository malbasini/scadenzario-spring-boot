package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ricevute")
public class Ricevuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_scadenza",nullable = false)
    private Integer idScadenza;


    @Column(name = "file_name",nullable = false, length = 150)
    private String nomeFile;

    @Column(name = "file_type",nullable = false, length = 500)
    private String tipoFile;

    @Column(name = "path",nullable = true, length = 450)
    private String path;

    @Lob
    @Column(name = "file_content", columnDefinition = "LONGBLOB")
    private byte[] content;

    @Column(name = "beneficiario",nullable = true, length = 150)
    private String beneficiario;

    @ManyToOne
    @JoinColumn(name = "scadenza_id", referencedColumnName = "id")
    private Scadenza scadenza;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        id = id;
    }

    public Integer getIdScadenza() {
        return idScadenza;
    }

    public void setIdScadenza(Integer idScadenza) {
        this.idScadenza = idScadenza;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public Scadenza getScadenza() {
        return scadenza;
    }

    public void setScadenza(Scadenza scadenza) {
        this.scadenza = scadenza;
    }
}
