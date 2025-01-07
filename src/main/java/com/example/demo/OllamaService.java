package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OllamaService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public String callOllamaForJSON(String userPrompt) throws Exception {
        // 1. Construct the JSON body to send to Ollama
        // We'll pass `stream: false` to get a single JSON response
        String requestBody = """
        {
            "model": "llama3.2",
            "prompt": "%s",
            "stream": false
        }
        """.formatted(
                createSystemPrompt() + "\n" + userPrompt
        );

        // 2. Make the POST request to Ollama
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity("http://localhost:11434/generate", requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            // 3. The response is a JSON object with a "response" field that holds the entire generation
            // e.g. { "response": "{... your JSON data ...}" }
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.get("response").asText();
        } else {
            throw new RuntimeException("Failed to get a valid response from Ollama");
        }
    }

    /**
     * This is our system-like prompt instructing Ollama how to format the output.
     */
    private String createSystemPrompt() {
        return """
        You are an AI that returns roadmap data in JSON format.
        Please ONLY return valid JSON. The JSON structure should look like:
        {
          "title": "string",
          "description": "string",
          "link": "string or null",
          "children": [
            {
              "title": "string",
              "description": "string",
              "link": "string or null",
              "children": [...]
            },
            ...
          ]
        }
        """;
    }

    /**
     * Parse the returned JSON (the "response" field) into our RoadmapNodeDTO recursively.
     */
    public RoadmapNodeDTO parseRoadmapJSON(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);

        RoadmapNodeDTO dto = new RoadmapNodeDTO();
        dto.setTitle(getText(root, "title"));
        dto.setDescription(getText(root, "description"));
        dto.setLink(getText(root, "link"));

        if (root.has("children") && root.get("children").isArray()) {
            for (JsonNode child : root.get("children")) {
                RoadmapNodeDTO childDTO = parseRoadmapJSON(child.toString());
                dto.getChildren().add(childDTO);
            }
        }
        return dto;
    }

    private String getText(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
    }
}
