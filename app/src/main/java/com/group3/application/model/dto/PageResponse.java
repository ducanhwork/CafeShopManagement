package com.group3.application.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Generic wrapper for paginated API responses from Spring Boot
 * Matches Spring Data Page structure
 */
public class PageResponse<T> {
    @SerializedName("content")
    private List<T> content;
    
    @SerializedName("totalElements")
    private long totalElements;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("size")
    private int size;
    
    @SerializedName("number")
    private int number;
    
    @SerializedName("numberOfElements")
    private int numberOfElements;
    
    @SerializedName("first")
    private boolean first;
    
    @SerializedName("last")
    private boolean last;
    
    @SerializedName("empty")
    private boolean empty;

    // Getters
    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }

    // Setters
    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
