package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoadmapService {

    private final OllamaService ollamaService;

    @Autowired
    private RoadmapNodeRepository roadmapNodeRepository;

    @Autowired
    private NodeExpandedRepository nodeExpandedRepository;

    public RoadmapService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    public RoadmapNodeDTO getRoadmapOnTheFly(String userPrompt,int retry_count) throws Exception {
        int retry_num =retry_count;
        int max_retries=10;
        // 1. Call Ollama to get raw JSON as a string
        String jsonString = ollamaService.callOllamaForJSON(userPrompt, ollamaService.createSystemPrompt());
        System.out.println("Received JSON from Ollama: " + jsonString);
        // 2. Parse into RoadmapNodeDTO tree
        RoadmapNodeDTO dto = ollamaService.parseRoadmapJSON(jsonString);
        //Basically, if we receive an answer with 0 children we should resend it once more
        if (dto.getChildren() == null || dto.getChildren().isEmpty()) {
            if (retry_num < max_retries) {
                retry_num++;
                return getRoadmapOnTheFly(userPrompt,retry_num);
            }
            //return getRoadmapOnTheFly(userPrompt);
        }
        // Convert DTO to Entity recursively
        RoadmapNode rootEntity = convertToEntity(dto, null);
        // Save the entire hierarchy (only root needs to be saved)
        // now its possible to traverse the roadmaprepo (with proper initialization) and retreive all of the nodes stored
        // this should also make it easier for UI
        roadmapNodeRepository.save(rootEntity);
        return dto;
        //return ollamaService.parseRoadmapJSON(jsonString);
    }

    public NodeExpandedDTO getNodeInfoOnTheFly(String title) throws Exception {
        NodeExpanded nodeInfo = nodeExpandedRepository.findByTitle(title);
        if (nodeInfo != null)
            return convertEntityToNode(nodeInfo);

        // 1. Call Ollama to get raw JSON as a string
        String jsonString = ollamaService.callOllamaForJSON(title, ollamaService.createNodePrompt());
        System.out.println("Received JSON from Ollama: " + jsonString);
        // 2. Parse into RoadmapNodeDTO tree
        NodeExpandedDTO dto = ollamaService.parseNodeJSON(jsonString);
        // Convert DTO to Entity recursively
        NodeExpanded rootEntity = convertNodeToEntity(dto);
        // Save the entire hierarchy (only root needs to be saved)
        // now its possible to traverse the roadmaprepo (with proper initialization) and retreive all of the nodes stored
        // this should also make it easier for UI
        nodeExpandedRepository.save(rootEntity);
        return dto;
        //return ollamaService.parseRoadmapJSON(jsonString);
    }

    private RoadmapNode convertToEntity(RoadmapNodeDTO dto, RoadmapNode parent) {
        RoadmapNode node = new RoadmapNode();
        node.setTitle(dto.getTitle());
        node.setDescription(dto.getDescription());
        node.setLink(dto.getLink());
        node.setParent(parent); // Set the parent reference

        // Recursively convert and add children
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            for (RoadmapNodeDTO childDTO : dto.getChildren()) {
                RoadmapNode child = convertToEntity(childDTO, node);
                node.getChildren().add(child); // This sets both parent and adds to the children list
            }
        }

        return node;
    }

    private NodeExpanded convertNodeToEntity(NodeExpandedDTO dto) {
        NodeExpanded node = new NodeExpanded();
        node.setTitle(dto.getTitle());
        node.setDescription(dto.getDescription());
        node.setExamples(dto.getExamples());
        node.setHowToLearn(dto.getHowToLearn());
        node.setLink(dto.getLink());

        return node;
    }

    private NodeExpandedDTO convertEntityToNode(NodeExpanded node) {
        NodeExpandedDTO dto = new NodeExpandedDTO();
        dto.setTitle(node.getTitle());
        dto.setDescription(node.getDescription());
        dto.setExamples(node.getExamples());
        dto.setHowToLearn(node.getHowToLearn());
        dto.setLink(node.getLink());

        return dto;
    }
}
