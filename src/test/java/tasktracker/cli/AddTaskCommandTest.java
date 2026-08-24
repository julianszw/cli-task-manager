package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class AddTaskCommandTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final AddTaskCommand command = new AddTaskCommand(service);

    @Test
    void executeWithNoArgsPrintsUsageAndCreatesNothing() {
        String output = captureOutput(() -> command.execute(new String[0]));

        assertTrue(output.contains("Uso: add"));
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void executeJoinsArgsAsTitleAndPrintsConfirmation() {
        String output = captureOutput(() -> command.execute(new String[]{"comprar", "leche", "hoy"}));

        assertTrue(output.contains("Tarea creada"));
        assertEquals(1, service.listTasks().size());
        assertEquals("comprar leche hoy", service.listTasks().get(0).getTitle());
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
