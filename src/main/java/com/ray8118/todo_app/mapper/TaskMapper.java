package com.ray8118.todo_app.mapper;

import com.ray8118.todo_app.dto.TaskRequest;
import com.ray8118.todo_app.dto.TaskResponse;
import com.ray8118.todo_app.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskMapper {

    private static final Logger logger = LoggerFactory.getLogger(TaskMapper.class);

    public static Task toEntity(TaskRequest request) {
        logger.debug("Mapping TaskRequest to Task entity: {}", request.getTitle());
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCompleted(false);
        logger.debug("Mapped Task entity: {}", task.getTitle());
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        logger.debug("Mapping Task entity to TaskResponse: {}", task.getTitle());
        TaskResponse response = new TaskResponse(
                task.getTodo_id(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate());
        logger.debug("Mapped TaskResponse: {}", response.getTitle());
        return response;
    }
}
