package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {
    private LearningPathService learningPathService;

    public QuestionController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }
    
    @PostMapping(path="/ask", produces="application/json")
    public Answer ask(@RequestBody Question question) {
        return learningPathService.askQuestion(question);
    }
}
