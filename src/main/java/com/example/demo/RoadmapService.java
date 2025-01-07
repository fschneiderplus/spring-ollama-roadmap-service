package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class RoadmapService {

    private final OllamaService ollamaService;

    public RoadmapService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    public RoadmapNodeDTO getRoadmapOnTheFly(String userPrompt) throws Exception {
        // 1. Call Ollama to get raw JSON as a string
        String jsonString = ollamaService.callOllamaForJSON(userPrompt);

        // 2. Parse into RoadmapNodeDTO tree
        return ollamaService.parseRoadmapJSON(jsonString);
    }
}
