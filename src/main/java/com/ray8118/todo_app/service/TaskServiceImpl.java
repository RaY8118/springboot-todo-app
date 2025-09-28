package com.ray8118.todo_app.service;

import java.util.List;
import java.util.Optional;

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
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<TaskResponse> getTaskById(Integer id) {
        return taskRepository.findById(id)
                .map(TaskMapper::toResponse);

    }

    @Override
    public TaskResponse getTaskOrThrow(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        return TaskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        Task task = TaskMapper.toEntity(request);
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Integer id, TaskRequest request) {
        Task existingTask = getTaskOrThrowEntity(id);
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setDueDate(request.getDueDate());
        Task updatedTask = taskRepository.save(existingTask);
        return TaskMapper.toResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Integer id) {
        Task existingTask = getTaskOrThrowEntity(id);
        existingTask.setCompleted(!existingTask.isCompleted());

        Task updatedTask = taskRepository.save(existingTask);
        return TaskMapper.toResponse(updatedTask);
    }

    @Override
    public List<TaskResponse> filterTasksByStatus(Boolean completed) {
        List<Task> tasks = (completed == null)
                ? taskRepository.findAll()
                : taskRepository.findByIsCompleted(completed);

        return tasks.stream().map(TaskMapper::toResponse).toList();
    }

    @Override
    public List<TaskResponse> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTask(Integer id) {
        Task existingTask = getTaskOrThrowEntity(id);
        taskRepository.delete(existingTask);
    }

    private Task getTaskOrThrowEntity(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

}
