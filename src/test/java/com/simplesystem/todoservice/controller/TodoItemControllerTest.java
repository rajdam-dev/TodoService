package com.simplesystem.todoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesystem.todoservice.dto.CreateTodoRequest;
import com.simplesystem.todoservice.dto.UpdateTodoRequest;
import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import com.simplesystem.todoservice.service.TodoItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoItemController.class)
public class TodoItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoItemService service;

    private TodoItem sampleTodo(Long id, TodoStatus status) {
        TodoItem item = TodoItem.create(
                "Test task",
                LocalDateTime.now().plusMinutes(10)
        );
        item.setId(id);
        item.setStatus(status);
        return item;
    }

    // test create
    @Test
    void createTodo() throws Exception {
        TodoItem saved = sampleTodo(1L, TodoStatus.NOT_DONE);

        when(service.create(any(), any())).thenReturn(saved);

        CreateTodoRequest req = new CreateTodoRequest();
        req.setDescription("Test task");
        req.setDueTime(saved.getDueTime());

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test task"))
                .andExpect(jsonPath("$.status").value("NOT_DONE"));
    }

    // test getAll
    @Test
    void getAllTodos() throws Exception {
        when(service.getAll(false))
                .thenReturn(List.of(sampleTodo(1L, TodoStatus.NOT_DONE)));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    // test getById
    @Test
    void getOneTodo() throws Exception {
        when(service.getById(1L))
                .thenReturn(sampleTodo(1L, TodoStatus.NOT_DONE));

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // test updateDescription
    @Test
    void updateTodo() throws Exception {
        TodoItem updated = sampleTodo(1L, TodoStatus.NOT_DONE);
        updated.setDescription("Updated");

        when(service.updateDescription(eq(1L), any())).thenReturn(updated);

        UpdateTodoRequest req = new UpdateTodoRequest();
        req.setDescription("Updated");

        mockMvc.perform(patch("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    // test markDone
    @Test
    void markDone() throws Exception {
        TodoItem done = sampleTodo(1L, TodoStatus.DONE);

        when(service.markDone(1L)).thenReturn(done);

        mockMvc.perform(put("/todos/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    // test markNotDone
    @Test
    void markNotDone() throws Exception {
        TodoItem notDone = sampleTodo(1L, TodoStatus.NOT_DONE);

        when(service.markNotDone(1L)).thenReturn(notDone);

        mockMvc.perform(put("/todos/1/not-done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_DONE"));
    }
}

