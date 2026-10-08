package com.mcotools.auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Une ligne d'historique : un utilisateur a genere un rapport PDF pour un
 * fichier source donne, a une date donnee. Enregistree par le frontend
 * juste apres l'appel a treatment (GET /rapportTraitement/pdf).
 */
@Entity
@Table(name = "rapport_genere")
public class RapportGenere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "source_file", nullable = false)
    private String sourceFile;

    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration;

    public RapportGenere() {
    }

    public RapportGenere(Long utilisateurId, String sourceFile, LocalDateTime dateGeneration) {
        this.utilisateurId = utilisateurId;
        this.sourceFile = sourceFile;
        this.dateGeneration = dateGeneration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }
}
