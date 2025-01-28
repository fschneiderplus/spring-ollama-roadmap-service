package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@Service
public class OllamaService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OllamaService(ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
        this.objectMapper = objectMapper;
    }

    public String callOllamaForJSON(String userPrompt, String promptTemplate) throws Exception {
        // Construct the JSON body using ObjectMapper
        ObjectNode requestNode = objectMapper.createObjectNode()
                .put("model", "llama3.2:latest")
                .put("temperature", 0.5)
                .put("prompt", promptTemplate + "\n" + userPrompt)
                .put("stream", false);

        String requestBody = objectMapper.writeValueAsString(requestNode);
        System.out.println("Sending request to Ollama with body: " + requestBody);

        try {
            // Make the POST request to Ollama
            return webClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSubscribe(s -> System.out.println("Subscribed to request"))
                    .doOnNext(response -> {
                        System.out.println("Received response from Ollama");
                        System.out.println("Raw response: " + response);
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            System.out.println("Parsed response: " + root.toString());
                            if (root.has("response")) {
                                System.out.println("Found response field: " + root.get("response").asText());
                            } else {
                                System.out.println("No response field found in: " + root.toString());
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing response: " + e.getMessage());
                        }
                    })
                    .doOnError(error -> {
                        System.err.println("Error occurred during request:");
                        System.err.println("Error type: " + error.getClass().getName());
                        System.err.println("Error message: " + error.getMessage());
                        if (error instanceof WebClientResponseException) {
                            WebClientResponseException wcError = (WebClientResponseException) error;
                            System.err.println("Response status: " + wcError.getStatusCode());
                            System.err.println("Response body: " + wcError.getResponseBodyAsString());
                        }
                    })
                    .map(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            return root.get("response").asText();
                        } catch (Exception e) {
                            System.err.println("Error parsing response in map: " + e.getMessage());
                            throw new RuntimeException("Failed to parse Ollama response", e);
                        }
                    })
                    .block();

        } catch (WebClientResponseException e) {
            System.err.println("WebClientResponseException occurred:");
            System.err.println("Status code: " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected exception: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * This is our system-like prompt instructing Ollama how to format the output.
     */
    public String createSystemPrompt() {
        return """
        You are an AI that returns roadmap data in JSON format.In your responce there should be no additional text, only json, also the root title should be in the formal - "Roadmap for - input".
        ONLY return VALID JSON. Do not add backticks or other special chars at the beginning or end.
        Field link for children should be in the format: "http://localhost:8080/api/node/{child_title}"
        There should be at least four children. There MUST also be children who themself have children.
        There must be a description.
        The parent of the child CANNOT be null. If there are no children, the "children" field should be an empty array.
        Roadmap title: 
        ONLY return valid JSON. The JSON structure should look like:
        {
          "title": "string",
          "description": "string",
          "link": "string or null",
          "children": [
            {
              "title": "string",
              "description": "string",
              "link": "string or null",
              "parent": "string",
              "children": [...]
            },
            ...
          ]
        }
        """;
    }

    /**
     * This is our system-like prompt instructing Ollama how to format the output.
     */
    public String createNodePrompt() {
        return """
        You are an AI that returns detailed information about topic in JSON format. In your response there should be no additional text, only json, also the title should be in the formal - "Explanation for - input".
        The link field in JSON should be filled with source or additional info if it exists. Otherwise leave this field as null.
        DO NOT put arrays into the json. Please ONLY return valid JSON. The JSON structure should look like:
        {
          "title": "string",
          "description": "string",
          "examples": "string",
          "howToLearn": "string",
          "link": "string or null"
        }
        Topic title: 
        """;
    }

    /**
     * Parse the returned JSON (the "response" field) into our RoadmapNodeDTO recursively.
     */
    public RoadmapNodeDTO parseRoadmapJSON(String json) throws Exception {
        //simplified the previous code
        System.out.println("Parsing JSON into RoadmapNodeDTO: " + json);
        RoadmapNodeDTO dto = objectMapper.readValue(json, RoadmapNodeDTO.class);
        System.out.println("Parsed RoadmapNodeDTO: " + dto);
        return dto;
    }

    /**
     * Parse the returned JSON (the "response" field) into our NodeExpandedDTO.
     */
    public NodeExpandedDTO parseNodeJSON(String json) throws Exception {
        //simplified the previous code
        NodeExpandedDTO dto = objectMapper.readValue(json, NodeExpandedDTO.class);
        System.out.println("Parsed NodeExpandedDTO: " + dto);
        return dto;
    }

    private String getText(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
    }

}
