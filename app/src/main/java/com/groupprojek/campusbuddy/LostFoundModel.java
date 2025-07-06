package com.groupprojek.campusbuddy;

public class LostFoundModel {
    int id;
    String title, description, imageUri;

    public LostFoundModel(int id, String title, String description, String imageUri) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUri() { return imageUri; }
}