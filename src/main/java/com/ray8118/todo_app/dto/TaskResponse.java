package com.ray8118.todo_app.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TaskResponse {
    private int todo_id;
    private String title;
    private String description;
    private boolean isCompleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    public TaskResponse() {
    }

    public TaskResponse(int todo_id, String title, String description, boolean isCompleted, LocalDate dueDate) {
        this.todo_id = todo_id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }

    public int getTodo_id() {
        return todo_id;
    }

    public void setTodo_id(int todo_id) {
        this.todo_id = todo_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

}
