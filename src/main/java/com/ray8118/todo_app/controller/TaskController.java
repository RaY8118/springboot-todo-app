package com.ray8118.todo_app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ray8118.todo_app.dto.TaskRequest;
import com.ray8118.todo_app.dto.TaskResponse;
import com.ray8118.todo_app.service.TaskService;

import jakarta.validation.Valid;

@RestController
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
        logger.info("TaskController initialized");
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks() {
        logger.info("Received request to get all tasks.");
        List<TaskResponse> tasks = taskService.getAllTasks();
        logger.info("Returning {} tasks.", tasks.size());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> addTask(@Valid @RequestBody TaskRequest taskRequest) {
        logger.info("Received request to create task: {}", taskRequest.getTitle());
        TaskResponse createdTask = taskService.createTask(taskRequest);
        logger.info("Task created successfully with ID: {}", createdTask.getTodo_id());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Integer id) {
        logger.info("Received request to get task with ID: {}", id);
        TaskResponse task = taskService.getTaskOrThrow(id);
        logger.info("Successfully retrieved task with ID: {}", task);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @GetMapping("/tasks/filter")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam(required = false) Boolean completed) {
        logger.info("Received request to filter tasks by completed status: {}", completed);
        List<TaskResponse> tasks = taskService.filterTasksByStatus(completed);
        logger.info("Returning {} filtered tasks.", tasks.size());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/tasks/search")
    public ResponseEntity<List<TaskResponse>> searchTasksByTitle(@RequestParam(required = false) String title) {
        logger.info("Received request to search tasks by title: {}", title);
        List<TaskResponse> tasks = taskService.searchTasksByTitle(title);
        logger.info("Returning {} tasks matching title search", tasks);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Integer id,
            @Valid @RequestBody TaskRequest taskDetails) {
        logger.info("Received request to update task with ID: {} and details : {}", id, taskDetails.getTitle());
        TaskResponse updatedTask = taskService.updateTask(id, taskDetails);
        logger.info("Task with ID {} updated", id);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Integer id) {
        logger.info("Received request to update status for task with ID : {}", id);
        TaskResponse updatedTask = taskService.updateStatus(id);
        logger.info("Status for task with ID {} updated successfully.", updatedTask.getTodo_id());
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Integer id) {
        logger.info("received request to delete task with ID: {}", id);
        taskService.deleteTask(id);
        logger.info("Task with ID {} deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
