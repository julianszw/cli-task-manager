package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class AddTaskCommandTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final AddTaskCommand command = new AddTaskCommand(service);

    @Test
    void executeWithNoArgsShowsUsageAndCreatesNothing() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        command.execute(new String[0], view);

        assertTrue(view.lastMessage().contains("Uso: add"));
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void executeJoinsArgsAsTitleAndShowsConfirmation() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        command.execute(new String[]{"comprar", "leche", "hoy"}, view);

        assertTrue(view.lastMessage().contains("Tarea creada"));
        assertEquals(1, service.listTasks().size());
        assertEquals("comprar leche hoy", service.listTasks().get(0).getTitle());
    }
}
