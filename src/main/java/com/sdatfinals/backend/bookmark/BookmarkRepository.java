package com.sdatfinals.backend.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    boolean existsByUserIdAndTopicSlug(Long userId, String topicSlug);

    void deleteByUserIdAndTopicSlug(Long userId, String topicSlug);
}
