package com.mcotools.auth.controller;

import com.mcotools.auth.dto.LoginRequest;
import com.mcotools.auth.dto.LoginResponse;
import com.mcotools.auth.dto.MessageResponse;
import com.mcotools.auth.dto.RegisterRequest;
import com.mcotools.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        log.debug("POST /api/auth/register - inscription de {}", request.getEmail());
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Inscription reussie. Verifiez votre email pour activer votre compte."));
        } catch (AuthService.EmailDejaUtiliseException e) {
            log.warn("POST /api/auth/register - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<MessageResponse> confirm(@RequestParam("token") String token) {
        log.debug("GET /api/auth/confirm - token={}", token);
        try {
            authService.confirmerActivation(token);
            return ResponseEntity.ok(new MessageResponse("Compte active avec succes."));
        } catch (AuthService.TokenActivationInvalideException e) {
            log.warn("GET /api/auth/confirm - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.debug("POST /api/auth/login - tentative de {}", request.getEmail());
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthService.IdentifiantsInvalidesException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (AuthService.CompteNonActifException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        }
    }
}
