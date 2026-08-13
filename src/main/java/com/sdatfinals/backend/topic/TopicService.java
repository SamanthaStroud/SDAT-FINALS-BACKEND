package com.sdatfinals.backend.topic;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic getTopicBySlug(String slug) {
        return topicRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Topic not found with slug: " + slug));
    }

    public Topic createTopic(Topic topic) {
        if (topicRepository.existsBySlug(topic.getSlug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Topic with slug '" + topic.getSlug() + "' already exists");
        }
        return topicRepository.save(topic);
    }

    public Topic updateTopic(Long id, Topic updatedTopic) {
        Topic existing = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Topic not found with id: " + id));

        existing.setSlug(updatedTopic.getSlug());
        existing.setName(updatedTopic.getName());
        existing.setDescription(updatedTopic.getDescription());
        existing.setCategory(updatedTopic.getCategory());
        existing.setCategorySymbol(updatedTopic.getCategorySymbol());
        existing.setColor(updatedTopic.getColor());
        existing.setAccentColor(updatedTopic.getAccentColor());
        existing.setBorderColor(updatedTopic.getBorderColor());

        return topicRepository.save(existing);
    }

    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Topic not found with id: " + id);
        }
        topicRepository.deleteById(id);
    }
}