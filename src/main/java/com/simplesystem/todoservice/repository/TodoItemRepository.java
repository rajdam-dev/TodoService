package com.simplesystem.todoservice.repository;


import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByStatusIn(List<TodoStatus> statuses);
    List<TodoItem> findByStatusAndDueTimeBefore(TodoStatus status, LocalDateTime time);

}