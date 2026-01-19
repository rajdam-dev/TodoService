package com.simplesystem.todoservice.dto;

import com.simplesystem.todoservice.model.TodoStatus;
import lombok.Data;

@Data
public class UpdateTodoRequest {
    private String description;
}
