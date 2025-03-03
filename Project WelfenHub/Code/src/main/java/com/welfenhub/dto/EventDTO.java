package com.welfenhub.dto;

public class EventDTO {

    private String title;

    private String content;

    private String date;

    private String author;

    EventDTO() {}

    public EventDTO(String title, String content, String date, String author) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.author = author;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
