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
import java.util.NoSuchElementException;

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

    @Test
    void create_rejectsBlankDescription() {
        assertThatThrownBy(() ->
                service.create("   ", LocalDateTime.now().plusMinutes(5))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description");
    }

    @Test
    void updateDescription_updatesSuccessfully() {
        TodoItem item = service.create("Original", LocalDateTime.now().plusMinutes(10));

        TodoItem updated = service.updateDescription(item.getId(), "Updated desc");

        assertThat(updated.getDescription()).isEqualTo("Updated desc");

        TodoItem reloaded = repository.findById(item.getId()).orElseThrow();
        assertThat(reloaded.getDescription()).isEqualTo("Updated desc");
    }

    @Test
    void updateDescription_throwsIfItemNotFound() {
        assertThatThrownBy(() ->
                service.updateDescription(999L, "Updated")
        ).isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Todo item not found");
    }

    @Test
    void updateDescription_throwsIfItemIsPastDue() {
        TodoItem item = TodoItem.builder()
                .description("Task")
                .dueTime(LocalDateTime.now().minusMinutes(10))
                .creationTime(LocalDateTime.now().minusHours(1))
                .status(TodoStatus.PAST_DUE)
                .build();

        TodoItem saved = repository.save(item);

        assertThatThrownBy(() ->
                service.updateDescription(saved.getId(), "Updated")
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("past");
    }

    @Test
    void updateDescription_rejectsBlankDescription() {
        TodoItem item = service.create("Original", LocalDateTime.now().plusMinutes(10));

        assertThatThrownBy(() ->
                service.updateDescription(item.getId(), "   ")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description");
    }

    @Test
    void updateDueTime_updatesSuccessfully() {
        TodoItem item = service.create("Task", LocalDateTime.now().plusMinutes(10));

        LocalDateTime newDue = LocalDateTime.now().plusMinutes(30);

        TodoItem updated = service.updateDueTime(item.getId(), newDue);

        assertThat(updated.getDueTime()).isEqualTo(newDue);
    }

    @Test
    void updateDueTime_rejectsPastDate() {
        TodoItem item = service.create("Task", LocalDateTime.now().plusMinutes(10));

        assertThatThrownBy(() ->
                service.updateDueTime(item.getId(), LocalDateTime.now().minusMinutes(1))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    @Test
    void updateDueTime_rejectsIfDone() {
        TodoItem item = service.create("Task", LocalDateTime.now().plusMinutes(10));
        service.markDone(item.getId());

        assertThatThrownBy(() ->
                service.updateDueTime(item.getId(), LocalDateTime.now().plusMinutes(30))
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("NOT_DONE");
    }

    @Test
    void updateDueTime_rejectsIfPastDue() {
        TodoItem item = TodoItem.builder()
                .description("Task")
                .dueTime(LocalDateTime.now().minusMinutes(10))
                .creationTime(LocalDateTime.now().minusHours(1))
                .status(TodoStatus.PAST_DUE)
                .build();

        TodoItem saved = repository.save(item);

        assertThatThrownBy(() ->
                service.updateDueTime(saved.getId(), LocalDateTime.now().plusMinutes(30))
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("past");
    }

    @Test
    void updateDueTime_throwsIfItemNotFound() {
        assertThatThrownBy(() ->
                service.updateDueTime(999L, LocalDateTime.now().plusMinutes(10))
        ).isInstanceOf(NoSuchElementException.class);
    }

}
