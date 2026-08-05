package com.example.linearlayout2.models;

public class ApplicationRecord {
    private String id;
    private String userId;
    private String playerName;
    private String studentId;
    private String sport;
    private String position;
    private String gender;
    private String school;
    private String status; // "Pending", "Approved", "Under Review"
    private String submittedAt;

    public ApplicationRecord() {
    }

    public ApplicationRecord(String id, String userId, String playerName, String sport, String position, String gender, String school, String status, String submittedAt) {
        this.id = id;
        this.userId = userId;
        this.playerName = playerName;
        this.sport = sport;
        this.position = position;
        this.gender = gender;
        this.school = school;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public ApplicationRecord(String id, String userId, String playerName, String studentId, String sport, String position, String gender, String school, String status, String submittedAt) {
        this.id = id;
        this.userId = userId;
        this.playerName = playerName;
        this.studentId = studentId;
        this.sport = sport;
        this.position = position;
        this.gender = gender;
        this.school = school;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}
