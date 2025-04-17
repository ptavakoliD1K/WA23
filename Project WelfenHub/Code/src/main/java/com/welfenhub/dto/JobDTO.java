package com.welfenhub.dto;

public class JobDTO {

    private String title;
    private String content;
    private String url;
    private String color;

    public JobDTO() {
        // Leerer Konstruktor für JSON-Deserialisierung
    }

    public JobDTO(String title, String content, String url) {
        this.title = title;
        this.content = content;
        this.url = url;
    }

    public JobDTO(String title, String content, String url, String color) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
