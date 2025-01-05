package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
public class QuestionController {

    private final LearningPathService learningPathService;

    public QuestionController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    /**
     * Example endpoint: Generate a new RoadmapNode from a user prompt
     */
    @PostMapping("/generate")
    public RoadmapNode generateNode(@RequestBody PromptDTO promptDTO) {
        return learningPathService.generateNodeFromLLM(promptDTO.getPrompt());
    }

    /**
     * Example endpoint: Create a node manually (no LLM)
     */
    @PostMapping("/create")
    public RoadmapNode createNode(@RequestBody RoadmapNode node) {
        return learningPathService.createNode(node);
    }

    // Simple DTO for capturing the prompt from the request body
    public static class PromptDTO {
        private String prompt;
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }
}
