package com.mcotools.parser.dto;

public class RequestDto {

    private Long reqId;
    private String depositId;
    private String canal;
    private String affranchissement;
    private String login;
    private String etat;
    private Integer nbPlis;
    private String env2Batch;
    private java.time.LocalDateTime dateModif;
    private java.time.LocalDateTime dateCreation;
    private java.time.LocalDateTime dateProdEsperee;
    private java.time.LocalDateTime deblocageEnvelope;
    private String codeErreur;

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getAffranchissement() {
        return affranchissement;
    }

    public void setAffranchissement(String affranchissement) {
        this.affranchissement = affranchissement;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Integer getNbPlis() {
        return nbPlis;
    }

    public void setNbPlis(Integer nbPlis) {
        this.nbPlis = nbPlis;
    }

    public String getEnv2Batch() {
        return env2Batch;
    }

    public void setEnv2Batch(String env2Batch) {
        this.env2Batch = env2Batch;
    }

    public java.time.LocalDateTime getDateModif() {
        return dateModif;
    }

    public void setDateModif(java.time.LocalDateTime dateModif) {
        this.dateModif = dateModif;
    }

    public java.time.LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(java.time.LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public java.time.LocalDateTime getDateProdEsperee() {
        return dateProdEsperee;
    }

    public void setDateProdEsperee(java.time.LocalDateTime dateProdEsperee) {
        this.dateProdEsperee = dateProdEsperee;
    }

    public java.time.LocalDateTime getDeblocageEnvelope() {
        return deblocageEnvelope;
    }

    public void setDeblocageEnvelope(java.time.LocalDateTime deblocageEnvelope) {
        this.deblocageEnvelope = deblocageEnvelope;
    }

    public String getCodeErreur() {
        return codeErreur;
    }

    public void setCodeErreur(String codeErreur) {
        this.codeErreur = codeErreur;
    }

    @Override
    public String toString() {
        return "RequestDto{reqId=" + reqId + ", depositId=" + depositId + ", canal=" + canal
                + ", etat=" + etat + "}";
    }
}
