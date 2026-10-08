package com.mcotools.auth.security;

import com.mcotools.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Verifie manuellement le header "Authorization: Bearer <token>" sur les
 * endpoints proteges (historique). Volontairement simple - pas de
 * Spring Security complet, pour rester coherent avec le reste du projet
 * (mco/parser/treatment n'en utilisent pas non plus).
 */
@Component
public class AuthenticatedUser {

    public static class NonAuthentifieException extends RuntimeException {
        public NonAuthentifieException() {
            super("Authentification requise");
        }
    }

    private final JwtService jwtService;

    public AuthenticatedUser(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /** Retourne l'id de l'utilisateur authentifie, ou leve NonAuthentifieException. */
    public Long requireUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new NonAuthentifieException();
        }
        String token = header.substring("Bearer ".length());
        Claims claims = jwtService.validateAndParse(token);
        if (claims == null) {
            throw new NonAuthentifieException();
        }
        return jwtService.extractUserId(claims);
    }
}
