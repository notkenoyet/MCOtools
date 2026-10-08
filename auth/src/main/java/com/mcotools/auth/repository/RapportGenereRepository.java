package com.mcotools.auth.repository;

import com.mcotools.auth.models.RapportGenere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RapportGenereRepository extends JpaRepository<RapportGenere, Long> {

    List<RapportGenere> findByUtilisateurIdOrderByDateGenerationDesc(Long utilisateurId);
}
