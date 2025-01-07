package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping("/roadmap")
    public RoadmapNodeDTO generateRoadmap(@RequestBody PromptDTO prompt) throws Exception {
        // e.g. prompt = "Generate a roadmap for learning advanced NLP."
        return roadmapService.getRoadmapOnTheFly(prompt.getPrompt());
    }

    // Simple DTO for capturing the incoming prompt JSON
    public static class PromptDTO {
        private String prompt;
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }
}
