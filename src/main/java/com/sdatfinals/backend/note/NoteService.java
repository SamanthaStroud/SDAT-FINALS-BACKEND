package com.sdatfinals.backend.note;

import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.topic.TopicRepository;
import com.sdatfinals.backend.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final TopicRepository topicRepository;

    public NoteService(NoteRepository noteRepository, TopicRepository topicRepository) {
        this.noteRepository = noteRepository;
        this.topicRepository = topicRepository;
    }

    public List<NoteResponse> getNotesForUser(User currentUser) {
        return noteRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(NoteResponse::from)
                .toList();
    }

    public List<NoteResponse> getNotesForUserAndTopic(User currentUser, String topicSlug) {
        return noteRepository.findByUserIdAndTopicSlugOrderByCreatedAtDesc(currentUser.getId(), topicSlug)
                .stream()
                .map(NoteResponse::from)
                .toList();
    }

    public NoteResponse createNote(User currentUser, NoteRequest request) {
        Topic topic = topicRepository.findBySlug(request.getTopicSlug())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No topic found with slug: " + request.getTopicSlug()));

        Note note = new Note();
        note.setUser(currentUser);
        note.setTopic(topic);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        return NoteResponse.from(noteRepository.save(note));
    }

    public NoteResponse updateNote(User currentUser, Long id, NoteRequest request) {
        Note note = getOwnedNote(currentUser, id);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUpdatedAt(Instant.now());
        return NoteResponse.from(noteRepository.save(note));
    }

    public void deleteNote(User currentUser, Long id) {
        noteRepository.delete(getOwnedNote(currentUser, id));
    }

    private Note getOwnedNote(User currentUser, Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found with id: " + id));

        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this note");
        }
        return note;
    }
}
