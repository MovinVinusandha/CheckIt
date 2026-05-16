package com.example.checkit;

public class Task {
    private String title;
    private String subtitle;
    private boolean isCompleted;

    public Task(String title, String subtitle, boolean isCompleted) {
        this.title = title;
        this.subtitle = subtitle;
        this.isCompleted = isCompleted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
