package com.example.demo;

import jakarta.persistence.*;

@Entity
public class NodeExpanded {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 1000)
    private String description;
    @Column(length = 1000)
    private String examples;
    @Column(length = 1000)
    private String howToLearn;
    private String link;

    public NodeExpanded() {
    }

    public NodeExpanded(String title, String description, String examples, String howToLearn, String link) {
        this.title = title;
        this.description = description;
        this.examples = examples;
        this.howToLearn = howToLearn;
        this.link = link;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExamples() { return examples; }
    public void setExamples(String examples) { this.examples = examples; }

    public String getHowToLearn() { return howToLearn; }
    public void setHowToLearn(String howToLearn) { this.howToLearn = howToLearn; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
