package com.ray8118.todo_app.service;

import java.util.List;
import java.util.Optional;

import com.ray8118.todo_app.dto.TaskRequest;
import com.ray8118.todo_app.dto.TaskResponse;

public interface TaskService {
    List<TaskResponse> getAllTasks();

    Optional<TaskResponse> getTaskById(Integer id);

    TaskResponse getTaskOrThrow(Integer id);

    TaskResponse createTask(TaskRequest request);

    TaskResponse updateTask(Integer id, TaskRequest request);

    TaskResponse updateStatus(Integer id);

    List<TaskResponse> filterTasksByStatus(Boolean completed);

    List<TaskResponse> searchTasksByTitle(String title);

    void deleteTask(Integer id);
}
