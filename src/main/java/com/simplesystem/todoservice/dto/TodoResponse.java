package com.simplesystem.todoservice.dto;

import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TodoResponse {

    private Long id;
    private String description;
    private TodoStatus status;
    private LocalDateTime creationTime;
    private LocalDateTime dueTime;
    private LocalDateTime completionTime;

    public static TodoResponse fromEntity(TodoItem item) {
        TodoResponse dto = new TodoResponse();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setStatus(item.getStatus());
        dto.setCreationTime(item.getCreationTime());
        dto.setDueTime(item.getDueTime());
        dto.setCompletionTime(item.getCompletionTime());
        return dto;
    }
}
