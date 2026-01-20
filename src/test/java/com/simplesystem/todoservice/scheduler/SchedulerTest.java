package com.simplesystem.todoservice.scheduler;

import com.simplesystem.todoservice.model.TodoItem;
import com.simplesystem.todoservice.model.TodoStatus;
import com.simplesystem.todoservice.repository.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SchedulerTest {

    @Autowired
    TodoPastDueScheduler scheduler;

    @Autowired
    TodoItemRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    void marksOnlyOverdueNotDoneItemsAsPastDue() {
        // overdue NOT_DONE → should change to PAST_DUE
        TodoItem overdue = TodoItem.builder()
                .description("Overdue")
                .creationTime(LocalDateTime.now().minusHours(2))
                .dueTime(LocalDateTime.now().minusMinutes(10))
                .status(TodoStatus.NOT_DONE)
                .build();

        // future due time: NOT_DONE should not change
        TodoItem future = TodoItem.builder()
                .description("Future")
                .creationTime(LocalDateTime.now())
                .dueTime(LocalDateTime.now().plusHours(1))
                .status(TodoStatus.NOT_DONE)
                .build();

        // DONE should not change
        TodoItem done = TodoItem.builder()
                .description("Done")
                .creationTime(LocalDateTime.now().minusHours(2))
                .dueTime(LocalDateTime.now().minusMinutes(10))
                .status(TodoStatus.DONE)
                .build();

        repository.saveAll(List.of(overdue, future, done));

        // when
        scheduler.markPastDueItems();

        // then
        List<TodoItem> all = repository.findAll();

        TodoItem overdueAfter = all.stream()
                .filter(i -> i.getDescription().equals("Overdue"))
                .findFirst().orElseThrow();

        TodoItem futureAfter = all.stream()
                .filter(i -> i.getDescription().equals("Future"))
                .findFirst().orElseThrow();

        TodoItem doneAfter = all.stream()
                .filter(i -> i.getDescription().equals("Done"))
                .findFirst().orElseThrow();

        assertThat(overdueAfter.getStatus()).isEqualTo(TodoStatus.PAST_DUE);
        assertThat(futureAfter.getStatus()).isEqualTo(TodoStatus.NOT_DONE);
        assertThat(doneAfter.getStatus()).isEqualTo(TodoStatus.DONE);
    }
}
