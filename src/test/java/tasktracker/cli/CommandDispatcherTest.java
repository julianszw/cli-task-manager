package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class CommandDispatcherTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final CommandRegistry registry = new CommandRegistry();
    private final CommandDispatcher dispatcher = new CommandDispatcher(registry);

    public CommandDispatcherTest() {
        registry.register(new AddTaskCommand(service));
        registry.register(new ListTasksCommand());
        registry.register(new CompleteTaskCommand(service));
        registry.register(new PurgeCompletedCommand(service));
        registry.register(new ExitCommand());
    }

    @Test
    void dispatchesUnknownCommandWithMessage() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        dispatcher.dispatch("borrar", view);

        assertTrue(view.lastMessage().contains("Comando no reconocido: borrar"));
    }

    @Test
    void dispatchesAddCommand() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        dispatcher.dispatch("add comprar leche", view);

        assertEquals(1, service.listTasks().size());
        assertEquals("comprar leche", service.listTasks().get(0).getTitle());
    }

    @Test
    void dispatchesListCommand() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        dispatcher.dispatch("list", view);

        assertTrue(view.isTaskListShown());
    }

    @Test
    void dispatchesExitCommand() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        dispatcher.dispatch("exit", view);

        assertTrue(view.isExited());
    }

    @Test
    void catchesDomainExceptionAndShowsMessage() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        dispatcher.dispatch("complete 99", view);

        assertTrue(view.lastMessage().contains("No existe una tarea con id 99"));
    }

    @Test
    void ignoresEmptyInput() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        dispatcher.dispatch("   ", view);

        assertTrue(view.messages().isEmpty());
    }
}
