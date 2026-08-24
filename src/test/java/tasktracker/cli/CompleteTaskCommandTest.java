package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
    void executeWithNonNumericIdPrintsErrorAndDoesNotModify() {
        service.addTask("A");

        String output = captureOutput(() -> command.execute(new String[]{"abc"}));

        assertTrue(output.contains("El id debe ser un número"));
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void executeWithValidIdCompletesTask() {
        Task task = service.addTask("A");

        String output = captureOutput(() -> command.execute(new String[]{String.valueOf(task.getId())}));

        assertTrue(output.contains("completada"));
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void executeWithNonexistentIdPropagatesNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> command.execute(new String[]{"99"}));
    }

    private String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }
}
