package com.mcotools.auth.dto;

import com.mcotools.auth.models.RapportGenere;

import java.time.LocalDateTime;

public class RapportGenereDto {

    private Long id;
    private String sourceFile;
    private LocalDateTime dateGeneration;

    public RapportGenereDto(RapportGenere entity) {
        this.id = entity.getId();
        this.sourceFile = entity.getSourceFile();
        this.dateGeneration = entity.getDateGeneration();
    }

    public Long getId() {
        return id;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }
}
