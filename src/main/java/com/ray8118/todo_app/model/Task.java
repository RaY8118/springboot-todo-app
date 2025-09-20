package com.ray8118.todo_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Task { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int todo_id;
    
    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isCompleted;  

    public Task(){}

    public Task(String title, String description){
        this.title = title;
        this.description = description;
        this.isCompleted = false;
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

    @Override
    public String toString() {
        return "Task [todo_id=" + todo_id + ", title=" + title + ", description=" + description + ", isCompleted="
                + isCompleted + "]";
    }


}
