package com.ray8118.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ray8118.todo_app.model.Task;

public interface TaskRepository  extends JpaRepository<Task, Integer>{

}