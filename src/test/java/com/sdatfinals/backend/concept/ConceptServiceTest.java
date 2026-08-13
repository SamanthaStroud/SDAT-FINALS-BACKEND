package com.sdatfinals.backend.concept;

import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.topic.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConceptServiceTest {

    @Mock
    private ConceptRepository conceptRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private ConceptService conceptService;

    @Test
    void getConceptsByTopicSlug_returnsConceptsForThatTopic() {
        Concept concept1 = new Concept();
        concept1.setSlug("normalization");

        Concept concept2 = new Concept();
        concept2.setSlug("indexing");

        when(conceptRepository.findByTopicSlug("databases"))
                .thenReturn(List.of(concept1, concept2));

        List<Concept> result = conceptService.getConceptsByTopicSlug("databases");

        assertThat(result).hasSize(2);
    }

    @Test
    void getConceptBySlug_throwsNotFoundWhenMissing() {
        when(conceptRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> conceptService.getConceptBySlug("nonexistent")
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createConcept_savesWhenSlugUniqueAndTopicExists() {
        ConceptRequest request = new ConceptRequest();
        request.setTopicSlug("databases");
        request.setSlug("normalization");
        request.setName("Normalization");

        Topic topic = new Topic();
        topic.setSlug("databases");

        when(conceptRepository.existsBySlug("normalization")).thenReturn(false);
        when(topicRepository.findBySlug("databases")).thenReturn(Optional.of(topic));
        when(conceptRepository.save(any(Concept.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Concept result = conceptService.createConcept(request);

        assertThat(result.getSlug()).isEqualTo("normalization");
        assertThat(result.getTopic()).isEqualTo(topic);
        verify(conceptRepository, times(1)).save(any(Concept.class));
    }

    @Test
    void createConcept_throwsConflictWhenSlugAlreadyExists() {
        ConceptRequest request = new ConceptRequest();
        request.setSlug("normalization");

        when(conceptRepository.existsBySlug("normalization")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> conceptService.createConcept(request)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(409);
        verify(topicRepository, never()).findBySlug(any());
    }

    @Test
    void createConcept_throwsBadRequestWhenTopicDoesNotExist() {
        ConceptRequest request = new ConceptRequest();
        request.setSlug("new-concept");
        request.setTopicSlug("nonexistent-topic");

        when(conceptRepository.existsBySlug("new-concept")).thenReturn(false);
        when(topicRepository.findBySlug("nonexistent-topic")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> conceptService.createConcept(request)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
        verify(conceptRepository, never()).save(any());
    }

    @Test
    void deleteConcept_throwsNotFoundWhenIdMissing() {
        when(conceptRepository.existsById(999L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> conceptService.deleteConcept(999L)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
        verify(conceptRepository, never()).deleteById(any());
    }
}