package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuestion;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaQuestionService;

@RestController
@RequestMapping("/sarvasya/questions")
@RequiredArgsConstructor
public class SarvasyaQuestionController {

    private final SarvasyaQuestionService service;

    @PostMapping
    public ResponseEntity<SarvasyaQuestion> create(@RequestBody SarvasyaQuestion payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaQuestion>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaQuestion> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaQuestion> update(@PathVariable UUID id, @RequestBody SarvasyaQuestion payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








