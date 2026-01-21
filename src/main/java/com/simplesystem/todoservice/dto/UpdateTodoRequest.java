package com.simplesystem.todoservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateTodoRequest {
    private String description;
    private LocalDateTime dueTime;
}
