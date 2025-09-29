package com.ray8118.todo_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ray8118.todo_app.dto.TaskRequest;
import com.ray8118.todo_app.dto.TaskResponse;
import com.ray8118.todo_app.exception.TaskNotFoundException;
import com.ray8118.todo_app.service.TaskService;

@WebMvcTest
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTasks_shouldReturnListOfTasks() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTodo_id(1);
        taskResponse.setTitle("Test task");
        taskResponse.setDescription("Test Description");
        taskResponse.setCompleted(false);
        List<TaskResponse> tasks = Collections.singletonList(taskResponse);

        Mockito.when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));

    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTodo_id(1);
        taskResponse.setTitle("Test task");
        taskResponse.setDescription("Test Description");
        taskResponse.setCompleted(false);

        Mockito.when(taskService.getTaskOrThrow(1)).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.todo_id").value(1))
                .andExpect(jsonPath("$.title").value("Test task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createTask_shouldCreateTaskAndReturnIt() throws Exception {
        String taskRequestJson = """
                  {
                      "title": "New Task",
                      "description": "New Description",
                      "dueDate": "2025-10-01"
                }
                """;

        TaskResponse createdTaskResponse = new TaskResponse();
        createdTaskResponse.setTodo_id(1);
        createdTaskResponse.setTitle("New Task");
        createdTaskResponse.setDescription("New Description");
        createdTaskResponse.setCompleted(false);
        createdTaskResponse.setDueDate(LocalDate.parse("2025-10-01"));

        Mockito.when(taskService.createTask(any(TaskRequest.class))).thenReturn(createdTaskResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.todo_id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.dueDate").value("2025-10-01"));

    }

    @Test
    void searchTasks_shouldSearchTasksAndReturnIt() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTodo_id(1);
        taskResponse.setTitle("Test task");
        taskResponse.setDescription("Test Description");
        taskResponse.setCompleted(false);
        List<TaskResponse> tasks = Collections.singletonList(taskResponse);

        Mockito.when(taskService.filterTasksByStatus(false)).thenReturn(tasks);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/filter")
                .param("completed", "false")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));
    }

    @Test
    void searchTasksByTitle_shouldSearchTasksAndReturnIt() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTodo_id(1);
        taskResponse.setTitle("Test task");
        taskResponse.setDescription("Test Description");
        taskResponse.setCompleted(false);
        List<TaskResponse> tasks = Collections.singletonList(taskResponse);

        Mockito.when(taskService.searchTasksByTitle("Test task")).thenReturn(tasks);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/search")
                .param("title", "Test task")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));
    }

    @Test
    void updateTask_shouldUpdateTaskAndReturnIt() throws Exception {
        String taskRequestJson = """
                    {
                        "title": "Updated Task",
                        "description": "Updated Description",
                        "dueDate": "2025-10-05"
                    }
                """;

        TaskResponse updatedTask = new TaskResponse();
        updatedTask.setTodo_id(1);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setCompleted(false);
        updatedTask.setDueDate(LocalDate.parse("2025-10-05"));

        Mockito.when(taskService.updateTask(Mockito.eq(1), any(TaskRequest.class))).thenReturn(updatedTask);

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.todo_id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.dueDate").value("2025-10-05"));
    }

    @Test
    void updateStatus_shouldMarkTaskAsCompleted() throws Exception {
        TaskResponse completedTask = new TaskResponse();
        completedTask.setTodo_id(1);
        completedTask.setTitle("Test task");
        completedTask.setDescription("Test Description");
        completedTask.setCompleted(true);

        Mockito.when(taskService.updateStatus(1)).thenReturn(completedTask);

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/1/complete")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.todo_id").value(1))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTaskById_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTaskById_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        Mockito.when(taskService.getTaskOrThrow(99))
                .thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
