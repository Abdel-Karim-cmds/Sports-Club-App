package com.example.linearlayout2.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String studentId;
    private String userType; // "ADMIN", "CLUB_MANAGER", "ATHLETE"
    private long createdAt;

    public User() {
        // Default constructor required for Firebase Firestore serialization
    }

    public User(String uid, String name, String email, String userType, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    public User(String uid, String name, String email, String studentId, String userType, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.studentId = studentId;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    private String assignedClub;

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getAssignedClub() {
        return assignedClub;
    }

    public void setAssignedClub(String assignedClub) {
        this.assignedClub = assignedClub;
    }
}
