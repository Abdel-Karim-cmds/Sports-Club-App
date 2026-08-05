package com.example.linearlayout2.models;

public class Fixture {
    private String id;
    private String sport;
    private String homeTeam;
    private String awayTeam;
    private String homeScore;
    private String awayScore;
    private String date;
    private String venue;
    private String status; // "Upcoming", "Live", "Finished"

    public Fixture() {
    }

    public Fixture(String id, String sport, String homeTeam, String awayTeam, String homeScore, String awayScore, String date, String venue, String status) {
        this.id = id;
        this.sport = sport;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.date = date;
        this.venue = venue;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public String getHomeScore() { return homeScore; }
    public void setHomeScore(String homeScore) { this.homeScore = homeScore; }

    public String getAwayScore() { return awayScore; }
    public void setAwayScore(String awayScore) { this.awayScore = awayScore; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
