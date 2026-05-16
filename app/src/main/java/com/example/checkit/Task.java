package com.example.checkit;

import com.google.firebase.firestore.DocumentId;

public class Task {
    @DocumentId
    private String taskId;
    private String title;
    private String subtitle;
    private boolean isCompleted;

    public Task() {
        // Required for Firestore
    }

    public Task(String title, String subtitle, boolean isCompleted) {
        this.title = title;
        this.subtitle = subtitle;
        this.isCompleted = isCompleted;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
