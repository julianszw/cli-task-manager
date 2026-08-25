package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class TaskListWindowTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());

    @Test
    void emptyListShowsMessage() {
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.getStatusText().contains("No hay tareas cargadas"));
    }

    @Test
    void completeSelectedTask() {
        service.addTask("A");
        service.addTask("B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('c', false, false));

        assertEquals(TaskStatus.COMPLETED, service.listTasks().get(0).getStatus());
        assertEquals(TaskStatus.PENDING, service.listTasks().get(1).getStatus());
    }

    @Test
    void moveDownThenCompleteSelectsSecondTask() {
        service.addTask("A");
        service.addTask("B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke('c', false, false));

        assertEquals(TaskStatus.PENDING, service.listTasks().get(0).getStatus());
        assertEquals(TaskStatus.COMPLETED, service.listTasks().get(1).getStatus());
    }

    @Test
    void deleteSelectedTask() {
        service.addTask("A");
        service.addTask("B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('d', false, false));

        List<Task> tasks = service.listTasks();
        assertEquals(1, tasks.size());
        assertEquals("B", tasks.get(0).getTitle());
    }

    @Test
    void reopenSelectedTask() {
        Task task = service.addTask("A");
        service.completeTask(task.getId());
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('r', false, false));

        assertEquals(TaskStatus.PENDING, service.listTasks().get(0).getStatus());
    }

    @Test
    void purgeRemovesCompletedTasks() {
        service.addTask("Pendiente");
        Task completed = service.addTask("Completada");
        service.completeTask(completed.getId());
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('p', false, false));

        List<Task> tasks = service.listTasks();
        assertEquals(1, tasks.size());
        assertEquals("Pendiente", tasks.get(0).getTitle());
    }

    @Test
    void purgeWithoutCompletedShowsMessage() {
        service.addTask("Pendiente");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('p', false, false));

        assertTrue(window.getStatusText().contains("No hay tareas completadas"));
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void exitKeysAreHandled() {
        service.addTask("A");
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke('q', false, false)));
        assertTrue(window.handleInput(new KeyStroke(KeyType.Escape)));
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void addKeyIsHandledWithoutGui() {
        service.addTask("A");
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke('a', false, false)));
        assertEquals(1, service.listTasks().size());
    }
}
