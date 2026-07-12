package com.mcotools.parser.dto;

import java.time.LocalDateTime;

public class DepositDto {

    private String depositId;
    private String partner;
    private String fileName;
    private String login;
    private String type;
    private String protocol;
    private String statut;
    private String codeErreur;
    private LocalDateTime dateRecept;
    private LocalDateTime dateModif;
    private String compte;

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

    @Override
    public String toString() {
        return "DepositDto{depositId=" + depositId + ", partner=" + partner + ", statut=" + statut + "}";
    }
}
