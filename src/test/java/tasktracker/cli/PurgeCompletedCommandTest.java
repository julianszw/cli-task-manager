package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        command.execute(new String[0], view);

        assertTrue(view.lastMessage().contains("eliminadas"));
        assertTrue(view.lastMessage().contains(task.getTitle()));
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void executeWithoutCompletedTasksShowsInformativeMessage() {
        service.addTask("Pendiente");
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        command.execute(new String[0], view);

        assertTrue(view.lastMessage().contains("No hay tareas completadas"));
        assertEquals(1, service.listTasks().size());
    }
}
