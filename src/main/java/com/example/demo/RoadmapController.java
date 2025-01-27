package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RoadmapController {

    private final RoadmapService roadmapService;
    public RoadmapController(RoadmapService roadmapService) {

        this.roadmapService = roadmapService;
    }

    @GetMapping("/roadmaps")
    public List<RoadmapNodeDTO> getAllRoadmaps(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return roadmapService.getAllRoadmaps(page, size).stream()
                .filter(roadmap -> roadmap.getChildren() != null && !roadmap.getChildren().isEmpty())
                .collect(Collectors.toList());
    }

    @PostMapping("/roadmap")
    public RoadmapNodeDTO generateRoadmap(@RequestBody PromptDTO prompt) throws Exception {
        return roadmapService.getRoadmapOnTheFly(prompt.getPrompt(), 0);
    }

    @GetMapping("/roadmaps/search")
    public List<RoadmapNodeDTO> searchRoadmaps(@RequestParam String query) {
        return roadmapService.searchRoadmaps(query);
    }

    // Simple DTO for capturing the incoming prompt JSON
    public static class PromptDTO {
        private String prompt;
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }
}
