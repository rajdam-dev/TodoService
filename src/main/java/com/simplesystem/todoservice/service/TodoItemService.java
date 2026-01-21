package com.simplesystem.todoservice.service;

import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import com.simplesystem.todoservice.repository.TodoItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TodoItemService {

    private final TodoItemRepository repository;

    public TodoItemService(TodoItemRepository repository) {
        this.repository = repository;
    }

    public TodoItem create(String description, LocalDateTime dueTime) {
        if (dueTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due time must be in the future");
        }

        TodoItem item = TodoItem.create(description, dueTime);
        return repository.save(item);
    }

    public List<TodoItem> getAll(boolean includeDone) {
        if (includeDone) {
            return repository.findAll();
        }
        return repository.findByStatusIn(
                List.of(TodoStatus.NOT_DONE, TodoStatus.PAST_DUE)
        );
    }

    public TodoItem getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo item not found: " + id));
    }

    public TodoItem updateDueTime(Long id, LocalDateTime newDueTime) {
        TodoItem item = getById(id);
        ensureNotPastDue(item);

        if (item.getStatus() != TodoStatus.NOT_DONE) {
            throw new IllegalStateException("Due time can only be changed when item is NOT_DONE");
        }

        if (newDueTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due time must be in the future");
        }

        item.setDueTime(newDueTime);
        return repository.save(item);
    }

    public TodoItem updateDescription(Long id, String newDescription) {
        TodoItem item = getById(id);
        ensureNotPastDue(item);

        if (newDescription == null || newDescription.isBlank()) {
            throw new IllegalArgumentException("Description must not be empty");
        }

        item.setDescription(newDescription);
        return repository.save(item);
    }

    public TodoItem markDone(Long id) {
        TodoItem item = getById(id);
        ensureNotPastDue(item);

        item.setStatus(TodoStatus.DONE);
        item.setCompletionTime(LocalDateTime.now());

        return repository.save(item);
    }

    public TodoItem markNotDone(Long id) {
        TodoItem item = getById(id);
        ensureNotPastDue(item);

        item.setStatus(TodoStatus.NOT_DONE);
        item.setCompletionTime(null);

        return repository.save(item);
    }

    private void ensureNotPastDue(TodoItem item) {
        if (item.getStatus() == TodoStatus.PAST_DUE) {
            throw new IllegalStateException("Cannot modify a past-due item");
        }
    }

}
