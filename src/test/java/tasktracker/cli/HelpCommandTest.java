package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class HelpCommandTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final CommandRegistry registry = new CommandRegistry();
    private final HelpCommand command = new HelpCommand(registry);

    @Test
    void getNameReturnsHelp() {
        assertEquals("help", command.getName());
    }

    @Test
    void executeListsAllRegisteredCommands() {
        registry.register(new AddTaskCommand(service));
        registry.register(new ListTasksCommand(service));
        registry.register(command);

        String output = captureOutput(() -> command.execute(new String[0]));

        assertTrue(output.contains("add"));
        assertTrue(output.contains("list"));
        assertTrue(output.contains("help"));
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
