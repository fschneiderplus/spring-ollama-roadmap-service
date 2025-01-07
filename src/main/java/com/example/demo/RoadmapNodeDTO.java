package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class RoadmapNodeDTO {
    private String title;
    private String description;
    private String link;
    private List<RoadmapNodeDTO> children = new ArrayList<>();

    public RoadmapNodeDTO() {}

    public RoadmapNodeDTO(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    // Getters and setters...
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public List<RoadmapNodeDTO> getChildren() { return children; }
    public void setChildren(List<RoadmapNodeDTO> children) { this.children = children; }
}
