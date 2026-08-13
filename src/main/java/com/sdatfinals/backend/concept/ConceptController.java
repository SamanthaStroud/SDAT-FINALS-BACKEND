package com.sdatfinals.backend.concept;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConceptController {

    private final ConceptService conceptService;

    public ConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @GetMapping("/concepts/{topicSlug}")
    public List<Concept> getConceptsByTopic(@PathVariable String topicSlug) {
        return conceptService.getConceptsByTopicSlug(topicSlug);
    }

    @GetMapping("/concept/{slug}")
    public Concept getConceptBySlug(@PathVariable String slug) {
        return conceptService.getConceptBySlug(slug);
    }

    @PostMapping("/concepts")
    @ResponseStatus(HttpStatus.CREATED)
    public Concept createConcept(@Valid @RequestBody ConceptRequest request) {
        return conceptService.createConcept(request);
    }

    @PatchMapping("/concepts/{id}")
    public Concept updateConcept(@PathVariable Long id, @RequestBody ConceptRequest request) {
        return conceptService.updateConcept(id, request);
    }

    @DeleteMapping("/concepts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConcept(@PathVariable Long id) {
        conceptService.deleteConcept(id);
    }
}