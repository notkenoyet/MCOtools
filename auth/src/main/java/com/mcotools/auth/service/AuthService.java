package com.mcotools.auth.service;

import com.mcotools.auth.dto.LoginRequest;
import com.mcotools.auth.dto.LoginResponse;
import com.mcotools.auth.dto.RegisterRequest;
import com.mcotools.auth.models.Utilisateur;
import com.mcotools.auth.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UtilisateurRepository utilisateurRepository, EmailService emailService, JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public static class EmailDejaUtiliseException extends RuntimeException {
        public EmailDejaUtiliseException(String email) {
            super("Un compte existe deja avec l'email " + email);
        }
    }

    public static class IdentifiantsInvalidesException extends RuntimeException {
        public IdentifiantsInvalidesException() {
            super("Email ou mot de passe incorrect");
        }
    }

    public static class CompteNonActifException extends RuntimeException {
        public CompteNonActifException() {
            super("Compte non active - verifiez votre email");
        }
    }

    public static class TokenActivationInvalideException extends RuntimeException {
        public TokenActivationInvalideException() {
            super("Lien d'activation invalide ou deja utilise");
        }
    }

    public void register(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative d'inscription avec un email deja utilise: {}", request.getEmail());
            throw new EmailDejaUtiliseException(request.getEmail());
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setActif(false);
        utilisateur.setTokenActivation(UUID.randomUUID().toString());
        utilisateur.setDateCreation(LocalDateTime.now());

        utilisateurRepository.save(utilisateur);
        log.info("Nouvel utilisateur inscrit (en attente d'activation): {}", utilisateur.getEmail());

        emailService.envoyerEmailActivation(utilisateur.getEmail(), utilisateur.getNom(), utilisateur.getTokenActivation());
    }

    public void confirmerActivation(String token) {
        Utilisateur utilisateur = utilisateurRepository.findByTokenActivation(token)
                .orElseThrow(TokenActivationInvalideException::new);

        utilisateur.setActif(true);
        utilisateur.setTokenActivation(null);
        utilisateurRepository.save(utilisateur);
        log.info("Compte active: {}", utilisateur.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Utilisateur> found = utilisateurRepository.findByEmail(request.getEmail());
        if (found.isEmpty() || !passwordEncoder.matches(request.getMotDePasse(), found.get().getMotDePasseHash())) {
            log.warn("Echec de connexion pour {}", request.getEmail());
            throw new IdentifiantsInvalidesException();
        }

        Utilisateur utilisateur = found.get();
        if (!utilisateur.isActif()) {
            log.warn("Tentative de connexion sur un compte non active: {}", request.getEmail());
            throw new CompteNonActifException();
        }

        String token = jwtService.generateToken(utilisateur.getId(), utilisateur.getEmail());
        log.info("Connexion reussie: {}", utilisateur.getEmail());
        return new LoginResponse(token, utilisateur.getNom(), utilisateur.getEmail());
    }
}
