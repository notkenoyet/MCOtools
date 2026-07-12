package com.mcotools.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "deposit")
public class Deposit {

    @Id
    @Column(name = "deposit_id")
    private String depositId;

    @Column(name = "partner")
    private String partner;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "login")
    private String login;

    @Column(name = "type")
    private String type;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "statut")
    private String statut;

    @Column(name = "code_erreur")
    private String codeErreur;

    @Column(name = "date_recept")
    private LocalDateTime dateRecept;

    @Column(name = "date_modif")
    private LocalDateTime dateModif;

    @Column(name = "compte")
    private String compte;

    public Deposit() {
    }

    public Deposit(String depositId, String partner, String fileName, String login, String type,
                    String protocol, String statut, String codeErreur, LocalDateTime dateRecept,
                    LocalDateTime dateModif, String compte) {
        this.depositId = depositId;
        this.partner = partner;
        this.fileName = fileName;
        this.login = login;
        this.type = type;
        this.protocol = protocol;
        this.statut = statut;
        this.codeErreur = codeErreur;
        this.dateRecept = dateRecept;
        this.dateModif = dateModif;
        this.compte = compte;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCodeErreur() {
        return codeErreur;
    }

    public void setCodeErreur(String codeErreur) {
        this.codeErreur = codeErreur;
    }

    public LocalDateTime getDateRecept() {
        return dateRecept;
    }

    public void setDateRecept(LocalDateTime dateRecept) {
        this.dateRecept = dateRecept;
    }

    public LocalDateTime getDateModif() {
        return dateModif;
    }

    public void setDateModif(LocalDateTime dateModif) {
        this.dateModif = dateModif;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }
}
