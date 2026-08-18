package com.sdatfinals.backend.note;

import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.topic.TopicRepository;
import com.sdatfinals.backend.user.User;
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
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private NoteService noteService;

    private User owner() {
        User user = new User();
        user.setId(1L);
        user.setEmail("owner@example.com");
        return user;
    }

    private Topic topic() {
        Topic topic = new Topic();
        topic.setId(10L);
        topic.setSlug("databases");
        return topic;
    }

    @Test
    void createNote_savesNoteUnderCurrentUserAndRequestedTopic() {
        User user = owner();
        Topic topic = topic();

        NoteRequest request = new NoteRequest();
        request.setTopicSlug("databases");
        request.setTitle("Normal forms");
        request.setContent("1NF, 2NF, 3NF...");

        when(topicRepository.findBySlug("databases")).thenReturn(Optional.of(topic));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        NoteResponse result = noteService.createNote(user, request);

        assertThat(result.title()).isEqualTo("Normal forms");
        assertThat(result.topicSlug()).isEqualTo("databases");
    }

    @Test
    void createNote_throwsBadRequestWhenTopicDoesNotExist() {
        NoteRequest request = new NoteRequest();
        request.setTopicSlug("nonexistent");

        when(topicRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> noteService.createNote(owner(), request)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
        verify(noteRepository, never()).save(any());
    }

    @Test
    void updateNote_throwsForbiddenWhenNoteBelongsToAnotherUser() {
        User owner = owner();
        User someoneElse = new User();
        someoneElse.setId(2L);

        Note note = new Note();
        note.setId(5L);
        note.setUser(owner);
        note.setTopic(topic());

        when(noteRepository.findById(5L)).thenReturn(Optional.of(note));

        NoteRequest request = new NoteRequest();
        request.setTitle("Hijacked title");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> noteService.updateNote(someoneElse, 5L, request)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(403);
        verify(noteRepository, never()).save(any());
    }

    @Test
    void updateNote_updatesTitleAndContentWhenOwnedByCurrentUser() {
        User owner = owner();
        Note note = new Note();
        note.setId(5L);
        note.setUser(owner);
        note.setTopic(topic());
        note.setTitle("Old title");
        note.setContent("Old content");

        when(noteRepository.findById(5L)).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteRequest request = new NoteRequest();
        request.setTitle("New title");
        request.setContent("New content");

        NoteResponse result = noteService.updateNote(owner, 5L, request);

        assertThat(result.title()).isEqualTo("New title");
        assertThat(result.content()).isEqualTo("New content");
    }

    @Test
    void deleteNote_throwsNotFoundWhenMissing() {
        when(noteRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> noteService.deleteNote(owner(), 999L)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void getNotesForUser_returnsNotesFromRepositoryOrderedByCreatedAt() {
        User user = owner();
        Note note = new Note();
        note.setId(1L);
        note.setUser(user);
        note.setTopic(topic());
        note.setTitle("A note");

        when(noteRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(note));

        List<NoteResponse> result = noteService.getNotesForUser(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("A note");
    }
}
