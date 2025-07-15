package com.groupprojek.campusbuddy;
public class LostFoundModel {
    private String id;
    private String itemName;
    private String description;
    private String status;   // e.g., "lost"
    private long timestamp;

    // Empty constructor
    public LostFoundModel() { }

    // Full constructor
    public LostFoundModel(String id, String itemName, String description, String status, long timestamp) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.status = status;
        this.timestamp = timestamp;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // --- Setters ---
    public void setId(String id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}