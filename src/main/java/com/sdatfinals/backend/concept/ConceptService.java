package com.sdatfinals.backend.concept;

import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.topic.TopicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final TopicRepository topicRepository;

    public ConceptService(ConceptRepository conceptRepository, TopicRepository topicRepository) {
        this.conceptRepository = conceptRepository;
        this.topicRepository = topicRepository;
    }

    public List<Concept> getConceptsByTopicSlug(String topicSlug) {
        return conceptRepository.findByTopicSlug(topicSlug);
    }

    public Concept getConceptBySlug(String slug) {
        return conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Concept not found with slug: " + slug));
    }

    public Concept createConcept(ConceptRequest request) {
        if (conceptRepository.existsBySlug(request.getSlug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Concept with slug '" + request.getSlug() + "' already exists");
        }

        Topic topic = topicRepository.findBySlug(request.getTopicSlug())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No topic found with slug: " + request.getTopicSlug()));

        Concept concept = new Concept();
        applyRequestToConcept(concept, request, topic);
        return conceptRepository.save(concept);
    }

    public Concept updateConcept(Long id, ConceptRequest request) {
        Concept existing = conceptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Concept not found with id: " + id));

        Topic topic = topicRepository.findBySlug(request.getTopicSlug())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No topic found with slug: " + request.getTopicSlug()));

        applyRequestToConcept(existing, request, topic);
        return conceptRepository.save(existing);
    }

    public void deleteConcept(Long id) {
        if (!conceptRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Concept not found with id: " + id);
        }
        conceptRepository.deleteById(id);
    }

    private void applyRequestToConcept(Concept concept, ConceptRequest request, Topic topic) {
        concept.setTopic(topic);
        concept.setSlug(request.getSlug());
        concept.setName(request.getName());
        concept.setSimpleExplanation(request.getSimpleExplanation());
        concept.setGroup(request.getGroup());
        concept.setTechnicalExplanation(request.getTechnicalExplanation());
        concept.setDiagram(request.getDiagram());
        concept.setGlance(request.getGlance());
        concept.setCommonMistakes(request.getCommonMistakes());
        concept.setWhyItMatters(request.getWhyItMatters());
        concept.setCodeExamples(request.getCodeExamples());
        concept.setMiniChallenge(request.getMiniChallenge());
    }
}