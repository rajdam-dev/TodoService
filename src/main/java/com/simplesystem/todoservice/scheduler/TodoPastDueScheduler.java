package com.simplesystem.todoservice.scheduler;

import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import com.simplesystem.todoservice.repository.TodoItemRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TodoPastDueScheduler {

    private final TodoItemRepository repository;

    public TodoPastDueScheduler(TodoItemRepository repository) {
        this.repository = repository;
    }

    // Marks all NOT_DONE items as PAST_DUE
    // Use scheduler rate configuration passed in through application.properties
    @Scheduled(fixedRateString = "${todo.pastdue.check.rate-ms}")
    public void markPastDueItems() {
        List<TodoItem> overdueItems =
                repository.findByStatusAndDueTimeBefore(TodoStatus.NOT_DONE, LocalDateTime.now());

        if (overdueItems.isEmpty()) {
            return;
        }

        for (TodoItem item : overdueItems) {
            item.setStatus(TodoStatus.PAST_DUE);
        }

        repository.saveAll(overdueItems);
    }
}
