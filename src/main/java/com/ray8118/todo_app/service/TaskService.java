package com.ray8118.todo_app.service;

import java.util.List;
import java.util.Optional;

import com.ray8118.todo_app.model.Task;

public interface TaskService {
    List<Task> getAllTasks();

    Optional<Task> getTaskById(Integer id);

    Task getTaskOrThrow(Integer id);

    Task createTask(Task task);

    Task updateTask(Integer id, Task task);

    Task updateStatus(Integer id);

    void deleteTask(Integer id);
}
