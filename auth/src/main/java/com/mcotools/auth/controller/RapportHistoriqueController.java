package com.mcotools.auth.controller;

import com.mcotools.auth.dto.MessageResponse;
import com.mcotools.auth.dto.NewRapportRequest;
import com.mcotools.auth.dto.RapportGenereDto;
import com.mcotools.auth.models.RapportGenere;
import com.mcotools.auth.repository.RapportGenereRepository;
import com.mcotools.auth.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Historique des rapports generes par l'utilisateur connecte. Proteges par
 * JWT (header Authorization: Bearer ...), verifie via AuthenticatedUser.
 */
@RestController
@RequestMapping("/api/historique")
public class RapportHistoriqueController {

    private static final Logger log = LoggerFactory.getLogger(RapportHistoriqueController.class);

    private final RapportGenereRepository rapportGenereRepository;
    private final AuthenticatedUser authenticatedUser;

    public RapportHistoriqueController(RapportGenereRepository rapportGenereRepository, AuthenticatedUser authenticatedUser) {
        this.rapportGenereRepository = rapportGenereRepository;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping
    public ResponseEntity<?> getHistorique(HttpServletRequest request) {
        try {
            Long userId = authenticatedUser.requireUserId(request);
            List<RapportGenereDto> historique = rapportGenereRepository
                    .findByUtilisateurIdOrderByDateGenerationDesc(userId)
                    .stream()
                    .map(RapportGenereDto::new)
                    .toList();
            log.info("GET /api/historique - {} rapport(s) pour l'utilisateur {}", historique.size(), userId);
            return ResponseEntity.ok(historique);
        } catch (AuthenticatedUser.NonAuthentifieException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        }
    }

    /** Appele par le frontend juste apres avoir genere/telecharge un rapport, pour l'ajouter a l'historique. */
    @PostMapping
    public ResponseEntity<?> enregistrerGeneration(HttpServletRequest request, @RequestBody NewRapportRequest body) {
        try {
            Long userId = authenticatedUser.requireUserId(request);
            RapportGenere entity = new RapportGenere(userId, body.getSourceFile(), LocalDateTime.now());
            rapportGenereRepository.save(entity);
            log.info("POST /api/historique - rapport {} enregistre pour l'utilisateur {}", body.getSourceFile(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RapportGenereDto(entity));
        } catch (AuthenticatedUser.NonAuthentifieException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        }
    }
}
