package com.simplesystem.todoservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class TodoItemTest {

    @Test
    void create_validInput_createsItem() {
        LocalDateTime due = LocalDateTime.now().plusMinutes(10);

        TodoItem item = TodoItem.create("Test task", due);

        assertThat(item.getDescription()).isEqualTo("Test task");
        assertThat(item.getDueTime()).isEqualTo(due);
        assertThat(item.getStatus()).isEqualTo(TodoStatus.NOT_DONE);
        assertThat(item.getCreationTime()).isNotNull();
        assertThat(item.getCompletionTime()).isNull();
    }

    @Test
    void create_nullDescription_throwsException() {
        LocalDateTime due = LocalDateTime.now().plusMinutes(10);

        assertThatThrownBy(() -> TodoItem.create(null, due))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description must not be empty");
    }

    @Test
    void create_blankDescription_throwsException() {
        LocalDateTime due = LocalDateTime.now().plusMinutes(10);

        assertThatThrownBy(() -> TodoItem.create("   ", due))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description must not be empty");
    }

    @Test
    void create_pastDueTime_throwsException() {
        LocalDateTime past = LocalDateTime.now().minusMinutes(1);

        assertThatThrownBy(() -> TodoItem.create("Task", past))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Due time must be in the future");
    }
}
