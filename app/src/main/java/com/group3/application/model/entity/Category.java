package com.group3.application.model.entity;

public class Category {
    private String id;
    private String name;
    private String description;

    public Category() {
    }

    public String getId() { return id; }
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
