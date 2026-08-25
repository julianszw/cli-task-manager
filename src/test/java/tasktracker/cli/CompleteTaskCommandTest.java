package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class CompleteTaskCommandTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final CompleteTaskCommand command = new CompleteTaskCommand(service);

    @Test
    void executeWithNonNumericIdShowsErrorAndDoesNotModify() {
        service.addTask("A");
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        command.execute(new String[]{"abc"}, view);

        assertTrue(view.lastMessage().contains("El id debe ser un número"));
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void executeWithValidIdCompletesTask() {
        Task task = service.addTask("A");
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        command.execute(new String[]{String.valueOf(task.getId())}, view);

        assertTrue(view.lastMessage().contains("completada"));
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void executeWithNonexistentIdPropagatesNotFoundException() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        assertThrows(TaskNotFoundException.class, () -> command.execute(new String[]{"99"}, view));
    }
}
