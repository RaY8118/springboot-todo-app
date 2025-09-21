package com.ray8118.todo_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ray8118.todo_app.model.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByIsCompleted(boolean completed);

    List<Task> findByTitleContainingIgnoreCase(String title);
}