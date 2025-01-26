package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadmapService {

    private final OllamaService ollamaService;

    @Autowired
    private RoadmapNodeRepository roadmapNodeRepository;

    public RoadmapService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    public RoadmapNodeDTO getRoadmapOnTheFly(String userPrompt, int retry_count) throws Exception {
        int retry_num = retry_count;
        int max_retries = 10;
        // 1. Call Ollama to get raw JSON as a string
        String jsonString = ollamaService.callOllamaForJSON(userPrompt);
        System.out.println("Received JSON from Ollama: " + jsonString);
        // Validate JSON
        try {
            new ObjectMapper().readTree(jsonString);
        } catch (Exception e) {
            System.err.println("Invalid JSON: " + jsonString);
            throw new RuntimeException("Invalid JSON received from Ollama");
        }
        // 2. Parse into RoadmapNodeDTO tree
        RoadmapNodeDTO dto = ollamaService.parseRoadmapJSON(jsonString);
        // Basically, if we receive an answer with 0 children we should resend it once more
        if (dto.getChildren() == null || dto.getChildren().isEmpty()) {
            if (retry_num < max_retries) {
                retry_num++;
                return getRoadmapOnTheFly(userPrompt, retry_num);
            }
        }
        // Convert DTO to Entity recursively
        RoadmapNode rootEntity = convertToEntity(dto, null);
        // Save the entire hierarchy (only root needs to be saved)
        roadmapNodeRepository.save(rootEntity);
        return dto;
    }



    public List<RoadmapNodeDTO> getAllRoadmaps() {
        List<RoadmapNode> roadmaps = roadmapNodeRepository.findAll();
        printRoadmaps(roadmaps);
        return roadmaps.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private void printRoadmaps(List<RoadmapNode> roadmaps) {
        for (RoadmapNode roadmap : roadmaps) {
            System.out.println("Roadmap: " + roadmap.getTitle());
            printRoadmap(roadmap, 1);
        }
    }

    private void printRoadmap(RoadmapNode node, int level) {
        for (RoadmapNode child : node.getChildren()) {
            System.out.println("  ".repeat(level) + "Node: " + child.getTitle());
            printRoadmap(child, level + 1);
        }
    }



    private RoadmapNodeDTO convertToDTO(RoadmapNode node) {
        RoadmapNodeDTO dto = new RoadmapNodeDTO(node.getTitle(), node.getDescription(), node.getLink());
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            List<RoadmapNodeDTO> children = node.getChildren().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        return dto;
    }

    private RoadmapNode convertToEntity(RoadmapNodeDTO dto, RoadmapNode parent) {
        RoadmapNode node = new RoadmapNode();
        node.setTitle(dto.getTitle());
        node.setDescription(dto.getDescription());
        node.setLink(dto.getLink());
        node.setParent(parent); // Set the parent reference

        // Recursively convert and add children
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            System.out.println("Converting children for: " + dto.getTitle());
            for (RoadmapNodeDTO childDTO : dto.getChildren()) {
                RoadmapNode child = convertToEntity(childDTO, node);
                System.out.println("Child: " + child);
                node.getChildren().add(child); // This sets both parent and adds to the children list
            }
        }

        return node;
    }


}
