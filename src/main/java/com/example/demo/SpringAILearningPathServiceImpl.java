package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SpringAILearningPathServiceImpl implements LearningPathService {
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final ObjectMapper objectMapper;
    private final OllamaService ollamaService;

    public SpringAILearningPathServiceImpl(
            RoadmapNodeRepository roadmapNodeRepository,
            ObjectMapper objectMapper,
            OllamaService ollamaService) {
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.objectMapper = objectMapper;
        this.ollamaService = ollamaService;
    }

    @Override
    public Answer askQuestion(Question question) {
        try {
            String answerText = ollamaService.callOllamaForJSON(question.question());
            return new Answer(answerText);
        } catch (Exception e) {
            e.printStackTrace();
            return new Answer("Sorry, I encountered an error while processing your question.");
        }
    }
    @Override
    public RoadmapNode generateNodeFromLLM(String prompt) {
        try {
            // 1. Call LLM service to get JSON describing the node
            String nodeJson = ollamaService.callOllamaForJSON(prompt);

            // 2. Parse JSON
            JsonNode jsonNode = objectMapper.readTree(nodeJson);

            // 3. Create RoadmapNode from JSON
            RoadmapNode newNode = new RoadmapNode();
            newNode.setTitle(jsonNode.get("title").asText());
            newNode.setDescription(jsonNode.get("description").asText());
            newNode.setLink(jsonNode.get("link").asText());

            // 4. Check if there's a parentId
            if (jsonNode.has("parentId") && !jsonNode.get("parentId").isNull()) {
                Long parentId = jsonNode.get("parentId").asLong();
                RoadmapNode parent = roadmapNodeRepository.findById(parentId).orElse(null);
                newNode.setParent(parent);
            }

            // 5. Save and return
            return roadmapNodeRepository.save(newNode);

        } catch (Exception e) {
            e.printStackTrace();
            // In production, handle this more gracefully
            return null;
        }
    }

    @Override
    public RoadmapNode createNode(RoadmapNode node) {
        return roadmapNodeRepository.save(node);
    }
}

