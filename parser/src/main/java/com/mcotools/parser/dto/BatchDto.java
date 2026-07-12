package com.mcotools.parser.dto;

import java.time.LocalDateTime;

public class BatchDto {

    private Long batId;
    private String reference;
    private String canal;
    private String type;
    private String affranchissement;
    private String etat;
    private String etab;
    private Integer nbEnv;
    private Integer nbFeuille;
    private Integer nbPage;
    private LocalDateTime dateModif;
    private LocalDateTime dateCreation;
    private String categorie;
    private String batErrorCode;

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

    @Override
    public String toString() {
        return "BatchDto{batId=" + batId + ", reference=" + reference + ", etat=" + etat + "}";
    }
}
