package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class CommandRegistryTest {

    @Test
    void findsRegisteredCommands() {
        CommandRegistry registry = new CommandRegistry();
        TaskService service = new TaskService(new InMemoryTaskRepository());
        registry.register(new AddTaskCommand(service));
        registry.register(new ListTasksCommand(service));
        registry.register(new CompleteTaskCommand(service));
        registry.register(new PurgeCompletedCommand(service));

        assertTrue(registry.contains("add"));
        assertTrue(registry.contains("list"));
        assertTrue(registry.contains("complete"));
        assertTrue(registry.contains("purge"));
    }

    @Test
    void findReturnsNullForUnknownCommand() {
        CommandRegistry registry = new CommandRegistry();

        assertFalse(registry.contains("delete"));
        assertNull(registry.find("delete"));
    }
}
