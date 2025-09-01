package com.example.testfrontendbackenddb.dto;

public class RoomTypeStats {
    private String type;
    private int total;
    private int occupied;
    private int available;

    public RoomTypeStats() {}

    public RoomTypeStats(String type, int total, int occupied, int available) {
        this.type = type;
        this.total = total;
        this.occupied = occupied;
        this.available = available;
    }

    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
}