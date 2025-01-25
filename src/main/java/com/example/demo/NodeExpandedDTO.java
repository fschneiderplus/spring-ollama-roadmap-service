package com.example.demo;

public class NodeExpandedDTO {
    private String title;
    private String description;
    private String examples;
    private String howToLearn;
    private String link;


    public NodeExpandedDTO() {}

    public NodeExpandedDTO(String title, String description, String examples, String howToLearn, String link) {
        this.title = title;
        this.description = description;
        this.examples = examples;
        this.howToLearn = howToLearn;
        this.link = link;
    }

    // Getters and setters...
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
