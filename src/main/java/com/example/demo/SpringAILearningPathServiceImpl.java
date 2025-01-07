package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SpringAILearningPathServiceImpl implements LearningPathService {

    private final RoadmapNodeRepository roadmapNodeRepository;
    private final ObjectMapper objectMapper; // For parsing JSON

    private final ChatClient chatClient;
    private final OllamaService ollamaService;

    // Could be an LLM integration or a mock
    public SpringAiLearningPathServiceImpl(RoadmapNodeRepository roadmapNodeRepository,
                                           ObjectMapper objectMapper, OllamaService ollamaService) {
        this.chatClient = chatClientBuilder.build();
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.objectMapper = objectMapper;
        this.ollamaService = ollamaService;
    }

    public SpringAILearningPathServiceImpl(RoadmapNodeRepository roadmapNodeRepository, ObjectMapper objectMapper, ChatClient chatClient) {
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.objectMapper = objectMapper;
        this.chatClient = chatClient;
    }


    @Override
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();
        return new Answer(answerText);
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

