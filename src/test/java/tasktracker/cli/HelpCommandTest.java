package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        registry.register(new ListTasksCommand());
        registry.register(command);

        FakeTaskTrackerView view = new FakeTaskTrackerView();
        command.execute(new String[0], view);

        assertTrue(view.lastMessage().contains("add"));
        assertTrue(view.lastMessage().contains("list"));
        assertTrue(view.lastMessage().contains("help"));
    }
}
