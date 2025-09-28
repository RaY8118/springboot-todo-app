package com.ray8118.todo_app.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ray8118.todo_app.dto.TaskRequest;
import com.ray8118.todo_app.dto.TaskResponse;
import com.ray8118.todo_app.exception.TaskNotFoundException;
import com.ray8118.todo_app.mapper.TaskMapper;
import com.ray8118.todo_app.model.Task;
import com.ray8118.todo_app.repository.TaskRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        logger.debug("Fetching all tasks from repository");
        List<TaskResponse> tasks = taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
        logger.debug("Found {} tasks.", tasks.size());
        return tasks;
    }

    @Override
    public Optional<TaskResponse> getTaskById(Integer id) {
        logger.debug("Attempting to retrieve task with ID: {}", id);
        Optional<TaskResponse> task = taskRepository.findById(id)
                .map(TaskMapper::toResponse);
        if (task.isPresent()) {
            logger.debug("Task with ID {} found.", id);
        } else {
            logger.debug("Task with ID {} not found.", id);
        }
        return task;

    }

    @Override
    public TaskResponse getTaskOrThrow(Integer id) {
        logger.debug("Attempting to retrieve task with ID : {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("TaskNotFoundException: Task with ID {} not found", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });

        logger.debug("Task with ID {} found", id);
        return TaskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        logger.info("Creating new task with title: {}", request.getTitle());
        Task task = TaskMapper.toEntity(request);
        Task savedTask = taskRepository.save(task);
        logger.info("Task created in DB with ID: {}", savedTask.getTodo_id());
        return TaskMapper.toResponse(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Integer id, TaskRequest request) {
        logger.info("Attempting to update task with ID: {} with details: {}", id, request.getTitle());
        Task existingTask = getTaskOrThrowEntity(id);
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setDueDate(request.getDueDate());
        Task updatedTask = taskRepository.save(existingTask);
        logger.info("Task with ID: {} updated successfully.", id);
        return TaskMapper.toResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Integer id) {
        logger.info("Attempting to update status for task with ID: {}", id);
        Task existingTask = getTaskOrThrowEntity(id);
        boolean newStatus = !existingTask.isCompleted();
        existingTask.setCompleted(newStatus);

        Task updatedTask = taskRepository.save(existingTask);
        logger.info("Status for task with ID: {} updated to {}.", id, newStatus);
        return TaskMapper.toResponse(updatedTask);
    }

    @Override
    public List<TaskResponse> filterTasksByStatus(Boolean completed) {
        logger.debug("Filtering tasks by completed status: {}", completed);
        List<Task> tasks = (completed == null)
                ? taskRepository.findAll()
                : taskRepository.findByIsCompleted(completed);

        List<TaskResponse> response = tasks.stream().map(TaskMapper::toResponse).toList();
        logger.debug("Found {} tasks with completed status: {}", response.size(), completed);
        return response;
    }

    @Override
    public List<TaskResponse> searchTasksByTitle(String title) {
        logger.debug("Searching tasks by title: {}", title);
        List<TaskResponse> response = taskRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
        logger.debug("Found {} tasks matching title: {}", response.size(), title);
        return response;
    }

    @Override
    @Transactional
    public void deleteTask(Integer id) {
        logger.info("Attempting to delete task with ID : {}", id);
        Task existingTask = getTaskOrThrowEntity(id);
        taskRepository.delete(existingTask);
        logger.info("Task with ID {} deleted from DB.", id);
    }

    private Task getTaskOrThrowEntity(Integer id) {
        logger.debug("Attempting to retrieve task entity with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("TaskNotFoundException: Task entity with ID {} not found", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        logger.debug("Task entity with ID {} found.", id);
        return task;
    }

}
