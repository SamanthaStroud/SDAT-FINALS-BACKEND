package com.sdatfinals.backend.concept;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConceptRequest {
    private String topicSlug;
    private String slug;
    private String name;
    private String simpleExplanation;
    private String group;
    private String technicalExplanation;
    private String diagram;
    private Object glance;
    private Object commonMistakes;
    private Object whyItMatters;
    private Object codeExamples;
    private Object miniChallenge;
}