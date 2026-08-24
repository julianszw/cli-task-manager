package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class PurgeCompletedCommandTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final PurgeCompletedCommand command = new PurgeCompletedCommand(service);

    @Test
    void executeWithCompletedTasksListsRemoved() {
        Task task = service.addTask("Completada");
        service.completeTask(task.getId());

        String output = captureOutput(() -> command.execute(new String[0]));

        assertTrue(output.contains("eliminadas"));
        assertTrue(output.contains(task.getTitle()));
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void executeWithoutCompletedTasksPrintsInformativeMessage() {
        service.addTask("Pendiente");

        String output = captureOutput(() -> command.execute(new String[0]));

        assertTrue(output.contains("No hay tareas completadas"));
        assertEquals(1, service.listTasks().size());
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
