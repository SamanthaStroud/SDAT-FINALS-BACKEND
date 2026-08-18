package com.sdatfinals.backend.bookmark;

import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "bookmarks", uniqueConstraints = @UniqueConstraint(
        name = "uk_bookmark_user_topic", columnNames = {"user_id", "topic_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
