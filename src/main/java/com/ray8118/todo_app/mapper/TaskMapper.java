package com.ray8118.todo_app.mapper;

import com.ray8118.todo_app.dto.TaskRequest;
import com.ray8118.todo_app.dto.TaskResponse;
import com.ray8118.todo_app.model.Task;

public class TaskMapper {
    public static Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCompleted(false);
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getTodo_id(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate());
    }
}
