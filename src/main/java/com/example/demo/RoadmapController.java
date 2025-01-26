package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoadmapController {

    private final RoadmapService roadmapService;
    public RoadmapController(RoadmapService roadmapService) {

        this.roadmapService = roadmapService;
    }

    @GetMapping("/roadmaps")
    public List<RoadmapNodeDTO> getAllRoadmaps() {
        return roadmapService.getAllRoadmaps();
    }

    @PostMapping("/roadmap")
    public RedirectView generateRoadmap(@RequestBody PromptDTO prompt) throws Exception {
        roadmapService.getRoadmapOnTheFly(prompt.getPrompt(), 0);
        return new RedirectView("/roadmaps.html");
    }

    // Simple DTO for capturing the incoming prompt JSON
    public static class PromptDTO {
        private String prompt;
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }
}
