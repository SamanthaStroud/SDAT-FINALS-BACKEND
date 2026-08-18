package com.sdatfinals.backend.note;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequest {

    @NotBlank(message = "topicSlug is required")
    private String topicSlug;

    private String title;
    private String content;
}
