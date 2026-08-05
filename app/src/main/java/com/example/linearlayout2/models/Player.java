package com.example.linearlayout2.models;

public class Player {
    private String id;
    private String name;
    private String sport;
    private String position;
    private String gender;
    private String school;
    private String phone;
    private String stats;

    public Player() {
    }

    public Player(String id, String name, String sport, String position, String gender, String school, String phone, String stats) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.position = position;
        this.gender = gender;
        this.school = school;
        this.phone = phone;
        this.stats = stats;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStats() { return stats; }
    public void setStats(String stats) { this.stats = stats; }
}
