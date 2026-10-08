package com.mcotools.auth.repository;

import com.mcotools.auth.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByTokenActivation(String tokenActivation);

    boolean existsByEmail(String email);
}
