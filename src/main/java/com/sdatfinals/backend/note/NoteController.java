package com.sdatfinals.backend.note;

import com.sdatfinals.backend.user.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponse> getAllNotes(@AuthenticationPrincipal UserPrincipal principal) {
        return noteService.getNotesForUser(principal.getUser());
    }

    @GetMapping("/topic/{topicSlug}")
    public List<NoteResponse> getNotesByTopic(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String topicSlug
    ) {
        return noteService.getNotesForUserAndTopic(principal.getUser(), topicSlug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse createNote(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody NoteRequest request
    ) {
        return noteService.createNote(principal.getUser(), request);
    }

    @PatchMapping("/{id}")
    public NoteResponse updateNote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody NoteRequest request
    ) {
        return noteService.updateNote(principal.getUser(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        noteService.deleteNote(principal.getUser(), id);
    }
}
