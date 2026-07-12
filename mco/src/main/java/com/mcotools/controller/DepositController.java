package com.mcotools.controller;

import com.mcotools.models.Deposit;
import com.mcotools.repository.DepositRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {

    private final DepositRepository depositRepository;

    public DepositController(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    @GetMapping
    public List<Deposit> getAll() {
        return depositRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposit> getById(@PathVariable String id) {
        Optional<Deposit> found = depositRepository.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Deposit> create(@RequestBody Deposit deposit) {
        Deposit saved = depositRepository.save(deposit);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposit> update(@PathVariable String id, @RequestBody Deposit deposit) {
        if (!depositRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        deposit.setDepositId(id);
        return ResponseEntity.ok(depositRepository.save(deposit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!depositRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        depositRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
