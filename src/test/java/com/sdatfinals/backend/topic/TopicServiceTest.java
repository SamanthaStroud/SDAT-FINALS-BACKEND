package com.sdatfinals.backend.topic;

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
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void getAllTopics_returnsAllTopicsFromRepository() {
        Topic topic1 = new Topic();
        topic1.setSlug("databases");
        topic1.setName("Databases");

        Topic topic2 = new Topic();
        topic2.setSlug("networking");
        topic2.setName("Networking");

        when(topicRepository.findAll()).thenReturn(List.of(topic1, topic2));

        List<Topic> result = topicService.getAllTopics();

        assertThat(result).hasSize(2);
        assertThat(result).contains(topic1, topic2);
    }

    @Test
    void getTopicBySlug_returnsTopicWhenFound() {
        Topic topic = new Topic();
        topic.setSlug("databases");
        topic.setName("Databases");

        when(topicRepository.findBySlug("databases")).thenReturn(Optional.of(topic));

        Topic result = topicService.getTopicBySlug("databases");

        assertThat(result.getName()).isEqualTo("Databases");
    }

    @Test
    void getTopicBySlug_throwsNotFoundWhenMissing() {
        when(topicRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> topicService.getTopicBySlug("nonexistent")
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createTopic_savesWhenSlugIsUnique() {
        Topic newTopic = new Topic();
        newTopic.setSlug("networking");
        newTopic.setName("Networking");

        when(topicRepository.existsBySlug("networking")).thenReturn(false);
        when(topicRepository.save(newTopic)).thenReturn(newTopic);

        Topic result = topicService.createTopic(newTopic);

        assertThat(result.getSlug()).isEqualTo("networking");
        verify(topicRepository, times(1)).save(newTopic);
    }

    @Test
    void createTopic_throwsConflictWhenSlugAlreadyExists() {
        Topic duplicateTopic = new Topic();
        duplicateTopic.setSlug("databases");

        when(topicRepository.existsBySlug("databases")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> topicService.createTopic(duplicateTopic)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(409);
        verify(topicRepository, never()).save(any());
    }

    @Test
    void deleteTopic_throwsNotFoundWhenIdMissing() {
        when(topicRepository.existsById(999L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> topicService.deleteTopic(999L)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
        verify(topicRepository, never()).deleteById(any());
    }
}