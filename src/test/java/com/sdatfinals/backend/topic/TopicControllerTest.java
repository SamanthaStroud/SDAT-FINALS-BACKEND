package com.sdatfinals.backend.topic;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class)
@AutoConfigureMockMvc(addFilters = false)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TopicService topicService;

    @Test
    void getAllTopics_returns200WithList() throws Exception {
        Topic topic = new Topic();
        topic.setSlug("databases");
        topic.setName("Databases");

        when(topicService.getAllTopics()).thenReturn(List.of(topic));

        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("databases"))
                .andExpect(jsonPath("$[0].name").value("Databases"));
    }

    @Test
    void getTopicBySlug_returns200WhenFound() throws Exception {
        Topic topic = new Topic();
        topic.setSlug("databases");
        topic.setName("Databases");

        when(topicService.getTopicBySlug("databases")).thenReturn(topic);

        mockMvc.perform(get("/api/topics/databases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Databases"));
    }

    @Test
    void createTopic_returns201WithCreatedTopic() throws Exception {
        Topic newTopic = new Topic();
        newTopic.setSlug("networking");
        newTopic.setName("Networking");

        when(topicService.createTopic(any(Topic.class))).thenReturn(newTopic);

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTopic)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("networking"));
    }

    @Test
    void deleteTopic_returns204() throws Exception {
        doNothing().when(topicService).deleteTopic(1L);

        mockMvc.perform(delete("/api/topics/1"))
                .andExpect(status().isNoContent());

        verify(topicService, times(1)).deleteTopic(1L);
    }
}