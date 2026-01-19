package com.simplesystem.todoservice.controller;

import com.simplesystem.todoservice.dto.*;
import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.service.TodoItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoItemController {

    private final TodoItemService service;

    public TodoItemController(TodoItemService service) {
        this.service = service;
    }

    // Create
    @PostMapping
    public TodoResponse create(@RequestBody CreateTodoRequest request) {
        TodoItem item = service.create(request.getDescription(), request.getDueTime());
        return TodoResponse.fromEntity(item);
    }

    // List
    @GetMapping
    public List<TodoResponse> getAll(@RequestParam(defaultValue = "false") boolean includeDone) {
        return service.getAll(includeDone)
                .stream()
                .map(TodoResponse::fromEntity)
                .toList();
    }

    // Get by id
    @GetMapping("/{id}")
    public TodoResponse getOne(@PathVariable Long id) {
        return TodoResponse.fromEntity(service.getById(id));
    }

    // Update description only
    @PatchMapping("/{id}")
    public TodoResponse updateDescription(
            @PathVariable Long id,
            @RequestBody UpdateTodoRequest request
    ) {
        TodoItem updated = service.updateDescription(id, request.getDescription());
        return TodoResponse.fromEntity(updated);
    }

    // Mark as DONE
    @PutMapping("/{id}/done")
    public TodoResponse markDone(@PathVariable Long id) {
        TodoItem updated = service.markDone(id);
        return TodoResponse.fromEntity(updated);
    }

    // Mark as NOT_DONE
    @PutMapping("/{id}/not-done")
    public TodoResponse markNotDone(@PathVariable Long id) {
        TodoItem updated = service.markNotDone(id);
        return TodoResponse.fromEntity(updated);
    }
}
