package com.example.linearlayout2.models;

public class Club {
    private String id;
    private String name;
    private String sport;
    private String description;
    private String managerEmail;
    private String status; // "Active", "Pending Setup"
    private long createdAt;

    public Club() {
    }

    public Club(String id, String name, String sport, String description, String managerEmail, String status, long createdAt) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.description = description;
        this.managerEmail = managerEmail;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
