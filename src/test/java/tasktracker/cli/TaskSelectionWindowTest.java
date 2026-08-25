package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class TaskSelectionWindowTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());

    @Test
    void enterCompletesSelectedTask() {
        service.addTask("A");
        service.addTask("B");
        TaskSelectionWindow window =
                new TaskSelectionWindow(service, TaskSelectionWindow.Mode.COMPLETE);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(TaskStatus.COMPLETED, service.listTasks().get(0).getStatus());
        assertEquals(TaskStatus.PENDING, service.listTasks().get(1).getStatus());
    }

    @Test
    void arrowDownThenEnterCompletesSecondTask() {
        service.addTask("A");
        service.addTask("B");
        TaskSelectionWindow window =
                new TaskSelectionWindow(service, TaskSelectionWindow.Mode.COMPLETE);

        window.handleInput(new KeyStroke(KeyType.ArrowDown));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(TaskStatus.PENDING, service.listTasks().get(0).getStatus());
        assertEquals(TaskStatus.COMPLETED, service.listTasks().get(1).getStatus());
    }

    @Test
    void enterReopensCompletedTask() {
        Task task = service.addTask("A");
        service.completeTask(task.getId());
        TaskSelectionWindow window =
                new TaskSelectionWindow(service, TaskSelectionWindow.Mode.REOPEN);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(TaskStatus.PENDING, service.listTasks().get(0).getStatus());
    }

    @Test
    void escapeCancelsWithoutModifying() {
        service.addTask("A");
        TaskSelectionWindow window =
                new TaskSelectionWindow(service, TaskSelectionWindow.Mode.COMPLETE);

        window.handleInput(new KeyStroke(KeyType.Escape));

        assertEquals(TaskStatus.PENDING, service.listTasks().get(0).getStatus());
    }

    @Test
    void emptyListShowsMessage() {
        TaskSelectionWindow window =
                new TaskSelectionWindow(service, TaskSelectionWindow.Mode.COMPLETE);

        assertTrue(window.getStatusText().contains("No hay tareas"));
    }
}
