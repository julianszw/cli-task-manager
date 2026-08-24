package tasktracker.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new InMemoryTaskRepository());
    }

    @Test
    void addTaskCreatesPendingTaskWithUniqueId() {
        Task first = service.addTask("Comprar leche");
        Task second = service.addTask("Pagar facturas");

        assertNotEquals(first.getId(), second.getId());
        assertEquals(TaskStatus.PENDING, first.getStatus());
        assertEquals("Comprar leche", first.getTitle());
    }

    @Test
    void addTaskRejectsEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> service.addTask(""));
    }

    @Test
    void addTaskRejectsBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> service.addTask("   "));
    }

    @Test
    void addTaskRejectsNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> service.addTask(null));
    }

    @Test
    void addTaskRejectsEmptyTitleAndDoesNotCreateTask() {
        service.addTask("Única");

        assertThrows(IllegalArgumentException.class, () -> service.addTask(" "));
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void listTasksReturnsEmptyWhenNoTasks() {
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void listTasksReturnsAllCreatedTasks() {
        service.addTask("A");
        service.addTask("B");

        List<Task> tasks = service.listTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    void completeTaskMarksExistingTaskCompleted() {
        Task task = service.addTask("Comprar leche");

        service.completeTask(task.getId());

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void completeTaskThrowsWhenTaskDoesNotExist() {
        assertThrows(TaskNotFoundException.class, () -> service.completeTask(99));
    }

    @Test
    void completeTaskIsIdempotentForAlreadyCompletedTask() {
        Task task = service.addTask("Comprar leche");
        service.completeTask(task.getId());

        assertDoesNotThrow(() -> service.completeTask(task.getId()));
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }
}
