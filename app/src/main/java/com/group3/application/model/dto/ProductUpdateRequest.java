package com.group3.application.model.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String name;

    private String description;

    private Double price;

    private String categoryName;
    private String status;

    public ProductUpdateRequest() {
    }

    public ProductUpdateRequest(String name, String description, Double price, String categoryName, String status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryName = categoryName;
        this.status = status;
    }
}
