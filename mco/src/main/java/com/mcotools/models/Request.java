package com.mcotools.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "request")
public class Request {

    @Id
    @Column(name = "req_id")
    private Long reqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id", insertable = false, updatable = false)
    private Deposit deposit;

    @Column(name = "deposit_id")
    private String depositId;

    @Column(name = "canal")
    private String canal;

    @Column(name = "affranchissement")
    private String affranchissement;

    @Column(name = "login")
    private String login;

    @Column(name = "etat")
    private String etat;

    @Column(name = "nb_plis")
    private Integer nbPlis;

    @Column(name = "env2batch")
    private String env2Batch;

    @Column(name = "date_modif")
    private LocalDateTime dateModif;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_prod_esperee")
    private LocalDateTime dateProdEsperee;

    @Column(name = "deblocage_envelope")
    private LocalDateTime deblocageEnvelope;

    @Column(name = "code_erreur")
    private String codeErreur;

    public Request() {
    }

    public Request(Long reqId, String depositId, String canal, String affranchissement, String login,
                    String etat, Integer nbPlis, String env2Batch, LocalDateTime dateModif,
                    LocalDateTime dateCreation, LocalDateTime dateProdEsperee,
                    LocalDateTime deblocageEnvelope, String codeErreur) {
        this.reqId = reqId;
        this.depositId = depositId;
        this.canal = canal;
        this.affranchissement = affranchissement;
        this.login = login;
        this.etat = etat;
        this.nbPlis = nbPlis;
        this.env2Batch = env2Batch;
        this.dateModif = dateModif;
        this.dateCreation = dateCreation;
        this.dateProdEsperee = dateProdEsperee;
        this.deblocageEnvelope = deblocageEnvelope;
        this.codeErreur = codeErreur;
    }

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
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

    public LocalDateTime getDateProdEsperee() {
        return dateProdEsperee;
    }

    public void setDateProdEsperee(LocalDateTime dateProdEsperee) {
        this.dateProdEsperee = dateProdEsperee;
    }

    public LocalDateTime getDeblocageEnvelope() {
        return deblocageEnvelope;
    }

    public void setDeblocageEnvelope(LocalDateTime deblocageEnvelope) {
        this.deblocageEnvelope = deblocageEnvelope;
    }

    public String getCodeErreur() {
        return codeErreur;
    }

    public void setCodeErreur(String codeErreur) {
        this.codeErreur = codeErreur;
    }
}
