package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class TaskTableFormatterTest {

    private final TaskTableFormatter formatter = new TaskTableFormatter();
    private final TaskService service = new TaskService(new InMemoryTaskRepository());

    @Test
    void renderShowsHeader() {
        service.addTask("Comprar leche");

        String output = formatter.render(service.listTasks());

        assertTrue(output.contains("ID"));
        assertTrue(output.contains("ESTADO"));
        assertTrue(output.contains("TÍTULO"));
    }

    @Test
    void renderReturnsEmptyWhenNoTasks() {
        assertTrue(formatter.render(service.listTasks()).isEmpty());
    }

    @Test
    void renderStrikesThroughCompletedRow() {
        Task task = service.addTask("Completada");
        service.completeTask(task.getId());

        String output = formatter.render(service.listTasks());

        assertTrue(output.contains("\033[9m"));
    }

    @Test
    void renderDoesNotStrikePendingRow() {
        service.addTask("Pendiente");

        String output = formatter.render(service.listTasks());

        assertFalse(output.contains("\033[9m"));
    }

    @Test
    void renderUsesDistinctColorsPerStatus() {
        service.addTask("Pendiente");
        Task completed = service.addTask("Completada");
        service.completeTask(completed.getId());

        String output = formatter.render(service.listTasks());

        assertTrue(output.contains("\033[33m"));
        assertTrue(output.contains("\033[32m"));
    }

    @Test
    void renderHighlightsOnlySelectedRow() {
        service.addTask("A");
        service.addTask("B");

        List<Task> tasks = service.listTasks();

        assertFalse(formatter.render(tasks).contains("\033[7m"));
        assertTrue(formatter.render(tasks, 1).contains("\033[7m"));
    }
}
