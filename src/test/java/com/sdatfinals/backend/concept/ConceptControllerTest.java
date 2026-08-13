package com.sdatfinals.backend.concept;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConceptController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConceptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConceptService conceptService;

    @Test
    void getConceptsByTopic_returns200WithList() throws Exception {
        Concept concept = new Concept();
        concept.setSlug("normalization");
        concept.setName("Normalization");

        when(conceptService.getConceptsByTopicSlug("databases")).thenReturn(List.of(concept));

        mockMvc.perform(get("/api/concepts/databases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("normalization"));
    }

    @Test
    void getConceptBySlug_returns200WhenFound() throws Exception {
        Concept concept = new Concept();
        concept.setSlug("normalization");
        concept.setName("Normalization");

        when(conceptService.getConceptBySlug("normalization")).thenReturn(concept);

        mockMvc.perform(get("/api/concept/normalization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Normalization"));
    }

    @Test
    void createConcept_returns201() throws Exception {
        ConceptRequest request = new ConceptRequest();
        request.setTopicSlug("databases");
        request.setSlug("indexing");
        request.setName("Indexing");

        Concept created = new Concept();
        created.setSlug("indexing");
        created.setName("Indexing");

        when(conceptService.createConcept(any(ConceptRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/concepts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("indexing"));
    }

    @Test
    void deleteConcept_returns204() throws Exception {
        doNothing().when(conceptService).deleteConcept(1L);

        mockMvc.perform(delete("/api/concepts/1"))
                .andExpect(status().isNoContent());

        verify(conceptService, times(1)).deleteConcept(1L);
    }
}