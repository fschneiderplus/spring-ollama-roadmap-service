package com.example.demo;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RoadmapNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String link;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private RoadmapNode parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoadmapNode> children = new ArrayList<>();

    public RoadmapNode() {
    }

    public RoadmapNode(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public RoadmapNode getParent() { return parent; }
    public void setParent(RoadmapNode parent) { this.parent = parent; }

    public List<RoadmapNode> getChildren() { return children; }
    public void setChildren(List<RoadmapNode> children) { this.children = children; }

}
