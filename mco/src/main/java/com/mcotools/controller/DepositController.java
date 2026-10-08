package com.mcotools.controller;

import com.mcotools.models.Deposit;
import com.mcotools.repository.DepositRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {

    private static final Logger log = LoggerFactory.getLogger(DepositController.class);

    private final DepositRepository depositRepository;

    public DepositController(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    @GetMapping
    public List<Deposit> getAll() {
        log.debug("GET /api/deposits - recuperation de tous les deposits");
        List<Deposit> deposits = depositRepository.findAll();
        log.info("GET /api/deposits - {} deposit(s) retourne(s)", deposits.size());
        return deposits;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposit> getById(@PathVariable String id) {
        log.debug("GET /api/deposits/{} - recherche du deposit", id);
        Optional<Deposit> found = depositRepository.findById(id);
        if (found.isEmpty()) {
            log.warn("GET /api/deposits/{} - deposit introuvable", id);
            return ResponseEntity.notFound().build();
        }
        log.info("GET /api/deposits/{} - deposit trouve", id);
        return ResponseEntity.ok(found.get());
    }

    @GetMapping("/source/{sourceFile}")
    public List<Deposit> getBySourceFile(@PathVariable String sourceFile) {
        log.debug("GET /api/deposits/source/{} - recuperation des deposits de ce fichier", sourceFile);
        List<Deposit> deposits = depositRepository.findBySourceFile(sourceFile);
        log.info("GET /api/deposits/source/{} - {} deposit(s) retourne(s)", sourceFile, deposits.size());
        return deposits;
    }

    @PostMapping
    public ResponseEntity<Deposit> create(@RequestBody Deposit deposit) {
        log.debug("POST /api/deposits - creation demandee: {}", deposit.getFileName());
        try {
            Deposit saved = depositRepository.save(deposit);
            log.info("POST /api/deposits - deposit cree avec id={}", saved.getDepositId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("POST /api/deposits - echec de creation du deposit: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposit> update(@PathVariable String id, @RequestBody Deposit deposit) {
        log.debug("PUT /api/deposits/{} - mise a jour demandee", id);
        if (!depositRepository.existsById(id)) {
            log.warn("PUT /api/deposits/{} - deposit introuvable, mise a jour annulee", id);
            return ResponseEntity.notFound().build();
        }
        deposit.setDepositId(id);
        try {
            Deposit updated = depositRepository.save(deposit);
            log.info("PUT /api/deposits/{} - deposit mis a jour", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("PUT /api/deposits/{} - echec de mise a jour: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.debug("DELETE /api/deposits/{} - suppression demandee", id);
        if (!depositRepository.existsById(id)) {
            log.warn("DELETE /api/deposits/{} - deposit introuvable, suppression annulee", id);
            return ResponseEntity.notFound().build();
        }
        depositRepository.deleteById(id);
        log.info("DELETE /api/deposits/{} - deposit supprime", id);
        return ResponseEntity.noContent().build();
    }
}
