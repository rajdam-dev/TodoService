package com.simplesystem.todoservice.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todo_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    @Column(nullable = false)
    private LocalDateTime dueTime;

    private LocalDateTime completionTime;

    // Factory method to create valid TodoItem
    public static TodoItem create(String description, LocalDateTime dueTime) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be empty");
        }
        if (dueTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due time must be in the future");
        }

        return TodoItem.builder()
                .description(description)
                .dueTime(dueTime)
                .creationTime(LocalDateTime.now())
                .status(TodoStatus.NOT_DONE)
                .build();
    }

}