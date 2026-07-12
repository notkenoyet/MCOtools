package com.mcotools.controller;

import com.mcotools.models.Batch;
import com.mcotools.repository.BatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchRepository batchRepository;

    public BatchController(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @GetMapping
    public List<Batch> getAll() {
        return batchRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getById(@PathVariable Long id) {
        Optional<Batch> found = batchRepository.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Batch> create(@RequestBody Batch batch) {
        Batch saved = batchRepository.save(batch);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Batch> update(@PathVariable Long id, @RequestBody Batch batch) {
        if (!batchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        batch.setBatId(id);
        return ResponseEntity.ok(batchRepository.save(batch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!batchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        batchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
