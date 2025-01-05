package com.example.demo;

public interface LearningPathService {
    Answer askQuestion(Question question);

    /**
     * Generate a new RoadmapNode from the LLM based on a prompt
     */
    RoadmapNode generateNodeFromLLM(String prompt);

    /**
     * Alternatively, create a node directly (if you want manual creation)
     */
    RoadmapNode createNode(RoadmapNode node);

}

