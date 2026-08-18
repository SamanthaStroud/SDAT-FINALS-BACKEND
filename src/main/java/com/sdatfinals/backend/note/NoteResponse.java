package com.sdatfinals.backend.note;

import java.time.Instant;

public record NoteResponse(
        Long id,
        String title,
        String content,
        String topicSlug,
        Instant createdAt,
        Instant updatedAt
) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getTopic().getSlug(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
