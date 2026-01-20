package com.simplesystem.todoservice.service;

import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import com.simplesystem.todoservice.repository.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TodoItemServiceTest {

    @Autowired
    TodoItemService service;

    @Autowired
    TodoItemRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    void create_createsNotDoneItem() {
        TodoItem item = service.create(
                "Test task",
                LocalDateTime.now().plusMinutes(10)
        );

        assertThat(item.getId()).isNotNull();
        assertThat(item.getStatus()).isEqualTo(TodoStatus.NOT_DONE);
        assertThat(item.getCreationTime()).isNotNull();
    }

    @Test
    void create_rejectsPastDueDate() {
        assertThatThrownBy(() ->
                service.create(
                        "Invalid",
                        LocalDateTime.now().minusMinutes(1)
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void markDone_setsDoneAndCompletionTime() {
        TodoItem item = service.create("Task", LocalDateTime.now().plusMinutes(5));

        TodoItem done = service.markDone(item.getId());

        assertThat(done.getStatus()).isEqualTo(TodoStatus.DONE);
        assertThat(done.getCompletionTime()).isNotNull();
    }

    @Test
    void markNotDone_clearsCompletionTime() {
        TodoItem item = service.create("Task", LocalDateTime.now().plusMinutes(5));
        service.markDone(item.getId());

        TodoItem notDone = service.markNotDone(item.getId());

        assertThat(notDone.getStatus()).isEqualTo(TodoStatus.NOT_DONE);
        assertThat(notDone.getCompletionTime()).isNull();
    }

    @Test
    void cannotModifyPastDueItem() {
        TodoItem item = TodoItem.builder()
                .description("Task")
                .dueTime(LocalDateTime.now().minusMinutes(10))
                .creationTime(LocalDateTime.now().minusHours(1))
                .status(TodoStatus.PAST_DUE)
                .build();

        TodoItem saved = repository.save(item);

        assertThatThrownBy(() -> service.markDone(saved.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getAll_excludesDoneAndPastDueByDefault() {
        service.create("A", LocalDateTime.now().plusMinutes(5));
        TodoItem b = service.create("B", LocalDateTime.now().plusMinutes(5));
        service.markDone(b.getId());

        List<TodoItem> result = service.getAll(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("A");
    }

    @Test
    void getAll_includeDone_returnsAll() {
        service.create("A", LocalDateTime.now().plusMinutes(5));
        TodoItem b = service.create("B", LocalDateTime.now().plusMinutes(5));
        service.markDone(b.getId());

        List<TodoItem> result = service.getAll(true);

        assertThat(result).hasSize(2);
    }
}
