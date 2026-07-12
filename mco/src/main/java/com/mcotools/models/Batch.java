package com.mcotools.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch")
public class Batch {

    @Id
    @Column(name = "bat_id")
    private Long batId;

    @Column(name = "reference")
    private String reference;

    @Column(name = "canal")
    private String canal;

    @Column(name = "type")
    private String type;

    @Column(name = "affranchissement")
    private String affranchissement;

    @Column(name = "etat")
    private String etat;

    @Column(name = "etab")
    private String etab;

    @Column(name = "nb_env")
    private Integer nbEnv;

    @Column(name = "nb_feuille")
    private Integer nbFeuille;

    @Column(name = "nb_page")
    private Integer nbPage;

    @Column(name = "date_modif")
    private LocalDateTime dateModif;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "bat_error_code")
    private String batErrorCode;

    public Batch() {
    }

    public Batch(Long batId, String reference, String canal, String type, String affranchissement,
                 String etat, String etab, Integer nbEnv, Integer nbFeuille, Integer nbPage,
                 LocalDateTime dateModif, LocalDateTime dateCreation, String categorie,
                 String batErrorCode) {
        this.batId = batId;
        this.reference = reference;
        this.canal = canal;
        this.type = type;
        this.affranchissement = affranchissement;
        this.etat = etat;
        this.etab = etab;
        this.nbEnv = nbEnv;
        this.nbFeuille = nbFeuille;
        this.nbPage = nbPage;
        this.dateModif = dateModif;
        this.dateCreation = dateCreation;
        this.categorie = categorie;
        this.batErrorCode = batErrorCode;
    }

    public Long getBatId() {
        return batId;
    }

    public void setBatId(Long batId) {
        this.batId = batId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAffranchissement() {
        return affranchissement;
    }

    public void setAffranchissement(String affranchissement) {
        this.affranchissement = affranchissement;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getEtab() {
        return etab;
    }

    public void setEtab(String etab) {
        this.etab = etab;
    }

    public Integer getNbEnv() {
        return nbEnv;
    }

    public void setNbEnv(Integer nbEnv) {
        this.nbEnv = nbEnv;
    }

    public Integer getNbFeuille() {
        return nbFeuille;
    }

    public void setNbFeuille(Integer nbFeuille) {
        this.nbFeuille = nbFeuille;
    }

    public Integer getNbPage() {
        return nbPage;
    }

    public void setNbPage(Integer nbPage) {
        this.nbPage = nbPage;
    }

    public LocalDateTime getDateModif() {
        return dateModif;
    }

    public void setDateModif(LocalDateTime dateModif) {
        this.dateModif = dateModif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getBatErrorCode() {
        return batErrorCode;
    }

    public void setBatErrorCode(String batErrorCode) {
        this.batErrorCode = batErrorCode;
    }
}
