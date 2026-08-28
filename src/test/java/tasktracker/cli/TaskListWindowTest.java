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
    private long inboxId;

    private long inbox() {
        inboxId = service.createList("Inbox").getId();
        return inboxId;
    }

    @Test
    void emptyListShowsMessage() {
        inbox();
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.getStatusText().contains("No hay tareas cargadas"));
    }

    @Test
    void completeSelectedTask() {
        inbox();
        service.addTask(inboxId, "A");
        service.addTask(inboxId, "B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('c', false, false));

        List<Task> tasks = service.listTasks(inboxId);
        assertEquals("B", tasks.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus());
        assertEquals("A", tasks.get(1).getTitle());
        assertEquals(TaskStatus.COMPLETED, tasks.get(1).getStatus());
    }

    @Test
    void moveDownThenCompleteSelectsSecondTask() {
        inbox();
        service.addTask(inboxId, "A");
        service.addTask(inboxId, "B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke('c', false, false));

        assertEquals(TaskStatus.PENDING, service.listTasks(inboxId).get(0).getStatus());
        assertEquals(TaskStatus.COMPLETED, service.listTasks(inboxId).get(1).getStatus());
    }

    @Test
    void deleteSelectedTask() {
        inbox();
        service.addTask(inboxId, "A");
        service.addTask(inboxId, "B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('d', false, false));

        List<Task> tasks = service.listTasks(inboxId);
        assertEquals(1, tasks.size());
        assertEquals("B", tasks.get(0).getTitle());
    }

    @Test
    void reopenSelectedTask() {
        inbox();
        Task task = service.addTask(inboxId, "A");
        service.completeTask(task.getId());
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('r', false, false));

        assertEquals(TaskStatus.PENDING, service.listTasks(inboxId).get(0).getStatus());
    }

    @Test
    void purgeRemovesCompletedTasksOfActiveList() {
        inbox();
        long work = service.createList("Trabajo").getId();

        service.addTask(inboxId, "Pendiente");
        Task completed = service.addTask(inboxId, "Completada");
        service.completeTask(completed.getId());

        Task workDone = service.addTask(work, "Otra completada");
        service.completeTask(workDone.getId());

        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('p', false, false));

        List<Task> inboxTasks = service.listTasks(inboxId);
        assertEquals(1, inboxTasks.size());
        assertEquals("Pendiente", inboxTasks.get(0).getTitle());
        assertEquals(1, service.listTasks(work).size());
    }

    @Test
    void purgeWithoutCompletedShowsMessage() {
        inbox();
        service.addTask(inboxId, "Pendiente");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke('p', false, false));

        assertTrue(window.getStatusText().contains("No hay tareas completadas"));
        assertEquals(1, service.listTasks(inboxId).size());
    }

    @Test
    void exitKeysAreHandledWithoutGui() {
        inbox();
        service.addTask(inboxId, "A");
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke('q', false, false)));
        assertTrue(window.handleInput(new KeyStroke(KeyType.Escape)));
        assertEquals(1, service.listTasks(inboxId).size());
    }

    @Test
    void addKeyIsHandledWithoutGui() {
        inbox();
        service.addTask(inboxId, "A");
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke('a', false, false)));
        assertEquals(1, service.listTasks(inboxId).size());
    }

    @Test
    void newListKeyIsHandledWithoutGui() {
        inbox();
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke('n', false, false)));
        assertEquals(1, service.listLists().size());
    }

    @Test
    void arrowDownMovesSelection() {
        inbox();
        service.addTask(inboxId, "A");
        service.addTask(inboxId, "B");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke(KeyType.ArrowDown));
        window.handleInput(new KeyStroke('c', false, false));

        assertEquals(TaskStatus.PENDING, service.listTasks(inboxId).get(0).getStatus());
        assertEquals(TaskStatus.COMPLETED, service.listTasks(inboxId).get(1).getStatus());
    }

    @Test
    void enterWithNoTasksDoesNothing() {
        inbox();
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke(KeyType.Enter)));
        assertTrue(service.listTasks(inboxId).isEmpty());
    }

    @Test
    void enterWithTasksWithoutGuiDoesNothing() {
        inbox();
        service.addTask(inboxId, "A");
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.handleInput(new KeyStroke(KeyType.Enter)));
        assertEquals(1, service.listTasks(inboxId).size());
    }

    @Test
    void tabCyclesToNextList() {
        service.createList("Inbox");
        long work = service.createList("Trabajo").getId();
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(1, window.activeListIndex());
        assertEquals(work, window.activeListId());
    }

    @Test
    void tabCyclesBackToFirstList() {
        service.createList("Inbox");
        service.createList("Trabajo");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(0, window.activeListIndex());
    }

    @Test
    void shiftTabCyclesToPreviousList() {
        service.createList("Inbox");
        service.createList("Trabajo");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke(KeyType.ReverseTab));

        assertEquals(1, window.activeListIndex());
    }

    @Test
    void tabWithSingleListDoesNothing() {
        inbox();
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(0, window.activeListIndex());
    }

    @Test
    void tasksAreScopedToActiveList() {
        long inbox = service.createList("Inbox").getId();
        service.addTask(inbox, "A");
        service.createList("Trabajo");
        TaskListWindow window = new TaskListWindow(service);

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(0, window.taskCount());
    }

    @Test
    void indicatorShowsActiveListNameAndPosition() {
        inbox();
        TaskListWindow window = new TaskListWindow(service);

        assertTrue(window.listIndicator().contains("Inbox"));
        assertTrue(window.listIndicator().contains("1/1"));
    }
}
