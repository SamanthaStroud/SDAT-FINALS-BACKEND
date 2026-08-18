package com.sdatfinals.backend.bookmark;

import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.topic.TopicRepository;
import com.sdatfinals.backend.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final TopicRepository topicRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, TopicRepository topicRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.topicRepository = topicRepository;
    }

    public List<String> getBookmarkedSlugs(User user) {
        return bookmarkRepository.findByUserId(user.getId())
                .stream()
                .map(bookmark -> bookmark.getTopic().getSlug())
                .toList();
    }

    public void addBookmark(User user, String topicSlug) {
        if (bookmarkRepository.existsByUserIdAndTopicSlug(user.getId(), topicSlug)) {
            return; // already bookmarked, treat as a no-op
        }

        Topic topic = topicRepository.findBySlug(topicSlug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No topic found with slug: " + topicSlug));

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setTopic(topic);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(User user, String topicSlug) {
        bookmarkRepository.deleteByUserIdAndTopicSlug(user.getId(), topicSlug);
    }
}
