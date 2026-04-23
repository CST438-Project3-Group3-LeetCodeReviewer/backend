package com.group3.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String difficulty; // Easy, Medium, Hard

    @ElementCollection
    private List<String> category;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    public Problem() {}

    public Problem(String title, String description, String difficulty, List<String> category, String starterCode) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.starterCode = starterCode;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }
}
