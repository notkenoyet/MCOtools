package com.mcotools.controller;

import com.mcotools.models.Batch;
import com.mcotools.repository.BatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private static final Logger log = LoggerFactory.getLogger(BatchController.class);

    private final BatchRepository batchRepository;

    public BatchController(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @GetMapping
    public List<Batch> getAll() {
        log.debug("GET /api/batches - recuperation de tous les batches");
        List<Batch> batches = batchRepository.findAll();
        log.info("GET /api/batches - {} batch(es) retourne(s)", batches.size());
        return batches;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getById(@PathVariable Long id) {
        log.debug("GET /api/batches/{} - recherche du batch", id);
        Optional<Batch> found = batchRepository.findById(id);
        if (found.isEmpty()) {
            log.warn("GET /api/batches/{} - batch introuvable", id);
            return ResponseEntity.notFound().build();
        }
        log.info("GET /api/batches/{} - batch trouve", id);
        return ResponseEntity.ok(found.get());
    }

    @GetMapping("/source/{sourceFile}")
    public List<Batch> getBySourceFile(@PathVariable String sourceFile) {
        log.debug("GET /api/batches/source/{} - recuperation des batches de ce fichier", sourceFile);
        List<Batch> batches = batchRepository.findBySourceFile(sourceFile);
        log.info("GET /api/batches/source/{} - {} batch(es) retourne(s)", sourceFile, batches.size());
        return batches;
    }

    @PostMapping
    public ResponseEntity<Batch> create(@RequestBody Batch batch) {
        log.debug("POST /api/batches - creation demandee: {}", batch.getReference());
        try {
            Batch saved = batchRepository.save(batch);
            log.info("POST /api/batches - batch cree avec id={}", saved.getBatId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("POST /api/batches - echec de creation du batch: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Batch> update(@PathVariable Long id, @RequestBody Batch batch) {
        log.debug("PUT /api/batches/{} - mise a jour demandee", id);
        if (!batchRepository.existsById(id)) {
            log.warn("PUT /api/batches/{} - batch introuvable, mise a jour annulee", id);
            return ResponseEntity.notFound().build();
        }
        batch.setBatId(id);
        try {
            Batch updated = batchRepository.save(batch);
            log.info("PUT /api/batches/{} - batch mis a jour", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("PUT /api/batches/{} - echec de mise a jour: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/batches/{} - suppression demandee", id);
        if (!batchRepository.existsById(id)) {
            log.warn("DELETE /api/batches/{} - batch introuvable, suppression annulee", id);
            return ResponseEntity.notFound().build();
        }
        batchRepository.deleteById(id);
        log.info("DELETE /api/batches/{} - batch supprime", id);
        return ResponseEntity.noContent().build();
    }
}
