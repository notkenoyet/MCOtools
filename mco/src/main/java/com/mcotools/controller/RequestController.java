package com.mcotools.controller;

import com.mcotools.models.Deposit;
import com.mcotools.models.Request;
import com.mcotools.repository.DepositRepository;
import com.mcotools.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    private final RequestRepository requestRepository;
    private final DepositRepository depositRepository;

    public RequestController(RequestRepository requestRepository, DepositRepository depositRepository) {
        this.requestRepository = requestRepository;
        this.depositRepository = depositRepository;
    }

    @GetMapping
    public List<Request> getAll() {
        log.debug("GET /api/requests - recuperation de tous les requests");
        List<Request> requests = requestRepository.findAll();
        log.info("GET /api/requests - {} request(s) retourne(s)", requests.size());
        return requests;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getById(@PathVariable Long id) {
        log.debug("GET /api/requests/{} - recherche du request", id);
        Optional<Request> found = requestRepository.findById(id);
        if (found.isEmpty()) {
            log.warn("GET /api/requests/{} - request introuvable", id);
            return ResponseEntity.notFound().build();
        }
        log.info("GET /api/requests/{} - request trouve", id);
        return ResponseEntity.ok(found.get());
    }

    @GetMapping("/source/{sourceFile}")
    public List<Request> getBySourceFile(@PathVariable String sourceFile) {
        log.debug("GET /api/requests/source/{} - recuperation des requests de ce fichier", sourceFile);
        List<Request> requests = requestRepository.findBySourceFile(sourceFile);
        log.info("GET /api/requests/source/{} - {} request(s) retourne(s)", sourceFile, requests.size());
        return requests;
    }

    @PostMapping
    public ResponseEntity<Request> create(@RequestBody Request request) {
        log.debug("POST /api/requests - creation demandee: depositId={}", request.getDepositId());
        ensureDepositExists(request.getDepositId());
        try {
            Request saved = requestRepository.save(request);
            log.info("POST /api/requests - request cree avec id={}", saved.getReqId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("POST /api/requests - echec de creation du request: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> update(@PathVariable Long id, @RequestBody Request request) {
        log.debug("PUT /api/requests/{} - mise a jour demandee", id);
        if (!requestRepository.existsById(id)) {
            log.warn("PUT /api/requests/{} - request introuvable, mise a jour annulee", id);
            return ResponseEntity.notFound().build();
        }
        request.setReqId(id);
        ensureDepositExists(request.getDepositId());
        try {
            Request updated = requestRepository.save(request);
            log.info("PUT /api/requests/{} - request mis a jour", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("PUT /api/requests/{} - echec de mise a jour: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/requests/{} - suppression demandee", id);
        if (!requestRepository.existsById(id)) {
            log.warn("DELETE /api/requests/{} - request introuvable, suppression annulee", id);
            return ResponseEntity.notFound().build();
        }
        requestRepository.deleteById(id);
        log.info("DELETE /api/requests/{} - request supprime", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Les rapports SICLv2 sont des deltas : une request peut referencer un deposit_id
     * qui n'apparait pas dans ce rapport (deja connu du systeme historiquement).
     * Pour ne pas rejeter la request avec une violation de cle etrangere, on cree
     * un deposit "squelette" minimal s'il n'existe pas encore ; il sera complete
     * plus tard si ce deposit apparait dans un rapport ulterieur.
     */
    private void ensureDepositExists(String depositId) {
        if (depositId == null || depositId.isBlank()) {
            return;
        }
        if (!depositRepository.existsById(depositId)) {
            log.warn("Deposit {} inconnu, creation d'un deposit squelette pour satisfaire la contrainte", depositId);
            Deposit stub = new Deposit();
            stub.setDepositId(depositId);
            depositRepository.save(stub);
        }
    }
}
