package com.sdatfinals.backend.note;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Note> findByUserIdAndTopicSlugOrderByCreatedAtDesc(Long userId, String topicSlug);
}
