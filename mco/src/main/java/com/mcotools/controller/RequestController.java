package com.mcotools.controller;

import com.mcotools.models.Request;
import com.mcotools.repository.RequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestRepository requestRepository;

    public RequestController(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public List<Request> getAll() {
        return requestRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getById(@PathVariable Long id) {
        Optional<Request> found = requestRepository.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Request> create(@RequestBody Request request) {
        Request saved = requestRepository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> update(@PathVariable Long id, @RequestBody Request request) {
        if (!requestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        request.setReqId(id);
        return ResponseEntity.ok(requestRepository.save(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!requestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        requestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
