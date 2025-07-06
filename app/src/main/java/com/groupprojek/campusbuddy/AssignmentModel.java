package com.groupprojek.campusbuddy;

public class AssignmentModel {
    private int id;
    private String title;
    private String dueDate;

    public AssignmentModel() {
    }

    public AssignmentModel(int id, String title, String dueDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}