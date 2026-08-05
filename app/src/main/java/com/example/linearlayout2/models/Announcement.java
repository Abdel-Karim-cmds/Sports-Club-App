package com.example.linearlayout2.models;

public class Announcement {
    private String id;
    private String title;
    private String content;
    private String date;
    private String category; // "Tryouts", "Notice", "Match", "Event"
    private String author;

    public Announcement() {
    }

    public Announcement(String id, String title, String content, String date, String category, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.category = category;
        this.author = author;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
