package com.ray8118.todo_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.dao.DataAccessResourceFailureException;
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
        void getAllTasks_shouldReturnEmptyList_whenNoTasksExist() throws Exception {
                Mockito.when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

                mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));
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
        void getTaskById_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Mockito.when(taskService.getTaskOrThrow(99))
                                .thenThrow(new TaskNotFoundException("Task not found"));

                mockMvc.perform(MockMvcRequestBuilders.get("/tasks/99")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
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
        void createTask_shouldReturnBadRequest_whenInvalidDueDate() throws Exception {
                String invalidTaskRequestJson = """
                                {
                                        "title": "Task with Bad Date",
                                        "description": "Description",
                                        "dueDate": "2025-XX-01"
                                }
                                """;

                mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidTaskRequestJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid date format. Please use YYYY-MM-DD."));
        }

        @Test
        void createTask_shouldCreateTaskWithNullDueDate() throws Exception {
                String taskRequestJson = """
                                 {
                                      "title": "Task without Due Date",
                                      "description": "Description without due date"
                                  }
                                """;

                TaskResponse createdTaskResponse = new TaskResponse();
                createdTaskResponse.setTodo_id(1);
                createdTaskResponse.setTitle("Task without Due Date");
                createdTaskResponse.setDescription("Description without due date");
                createdTaskResponse.setCompleted(false);
                createdTaskResponse.setDueDate(null);

                Mockito.when(taskService.createTask(any(TaskRequest.class))).thenReturn(createdTaskResponse);

                mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskRequestJson))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.todo_id").value(1))
                                .andExpect(jsonPath("$.title").value("Task without Due Date"))
                                .andExpect(jsonPath("$.dueDate").doesNotExist());
        }

        @Test
        void createTask_shouldReturnBadRequest_whenValidationFails() throws Exception {
                String invalidTaskRequestJson = """
                                {
                                        "title": "",
                                        "description": "New Description",
                                        "dueDate": "2025-10-01"
                                }
                                """;

                mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidTaskRequestJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value("Validation failed"));
        }

        @Test
        void createTask_shouldReturnInternalServerError() throws Exception {
                String taskRequestJson = """
                                {
                                        "title": "New task",
                                        "description": "New Description",
                                        "dueDate": "2025-10-01"
                                }
                                """;

                Mockito.when(taskService.createTask(any(TaskRequest.class)))
                                .thenThrow(new RuntimeException("Database is down"));

                mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskRequestJson))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message")
                                                .value("An unexpected error occurred: Database is down"));

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
        void searchTasks_shouldReturnEmptyList_whenNoMatchingTasks() throws Exception {
                Mockito.when(taskService.filterTasksByStatus(true)).thenReturn(Collections.emptyList());

                mockMvc.perform(MockMvcRequestBuilders.get("/tasks/filter")
                                .param("completed", "true")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));
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
        void searchTasksByTitle_shouldReturnEmptyList_whenNoMatchingTasks() throws Exception {
                Mockito.when(taskService.searchTasksByTitle("NonExistentTitle")).thenReturn(Collections.emptyList());

                mockMvc.perform(MockMvcRequestBuilders.get("/tasks/search")
                                .param("title", "NonExistentTitle")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));
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
        void updateTask_shouldReturn404_whenTaskNotFound() throws Exception {
                String updateRequestJson = """
                                {
                                    "title": "Updated Task",
                                    "description": "Updated Description",
                                    "dueDate": "2025-10-15",
                                    "completed": true
                                }
                                """;

                Mockito.when(taskService.updateTask(Mockito.eq(99), any(TaskRequest.class)))
                                .thenThrow(new TaskNotFoundException("Task with ID 99 not found"));

                mockMvc.perform(MockMvcRequestBuilders.put("/tasks/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequestJson))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task with ID 99 not found"));
        }

        @Test
        void updateTask_shouldReturn500_whenDatabaseErrorOccurs() throws Exception {
                String updateRequestJson = """
                                {
                                    "title": "Updated Task",
                                    "description": "Updated Description",
                                    "dueDate": "2025-10-15",
                                    "completed": true
                                }
                                """;

                Mockito.when(taskService.updateTask(eq(1), any(TaskRequest.class)))
                                .thenThrow(new DataAccessResourceFailureException("Database down"));

                mockMvc.perform(MockMvcRequestBuilders.put("/tasks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequestJson))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message").value("A database error occurred: Database down"));
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
        void updateStatus_shouldReturn404_whenTaskNotFound() throws Exception {
                Mockito.when(taskService.updateStatus(99))
                                .thenThrow(new TaskNotFoundException("Task with ID 99 not found"));

                mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/99/complete")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task with ID 99 not found"));
        }

        @Test
        void updateStatus_shouldReturn500_whenDatabaseErrorOccurs() throws Exception {
                Mockito.when(taskService.updateStatus(1))
                                .thenThrow(new DataAccessResourceFailureException("DB not reachable"));

                mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/1/complete")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message").value("A database error occurred: DB not reachable"));
        }

        @Test
        void deleteTaskById_shouldReturnNoContent() throws Exception {
                Mockito.doNothing().when(taskService).deleteTask(1);

                mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void deleteTask_shouldReturn404_whenTaskNotFound() throws Exception {
                Mockito.doThrow(new TaskNotFoundException("Task with ID 42 not found"))
                                .when(taskService).deleteTask(42);

                mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/42"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task with ID 42 not found"));
        }

        @Test
        void deleteTask_shouldReturn500_whenDatabaseErrorOccurs() throws Exception {
                Mockito.doThrow(new DataAccessResourceFailureException("DB not reachable"))
                                .when(taskService).deleteTask(1);

                mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message").value("A database error occurred: DB not reachable"));
        }

}
