package com.sdatfinals.backend.concept;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConceptRepository extends JpaRepository<Concept, Long> {

    Optional<Concept> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Concept> findByTopicSlug(String topicSlug);
}