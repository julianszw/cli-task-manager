package tasktracker.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exception.TaskListNotFoundException;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.repository.TaskRepository;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new InMemoryTaskRepository());
    }

    private long inboxId() {
        return service.createList("Inbox").getId();
    }

    @Test
    void createListCreatesListWithUniqueId() {
        TaskList first = service.createList("Inbox");
        TaskList second = service.createList("Trabajo");

        assertNotEquals(first.getId(), second.getId());
        assertEquals("Inbox", first.getName());
        assertEquals("Trabajo", second.getName());
    }

    @Test
    void createListRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> service.createList(""));
    }

    @Test
    void createListRejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> service.createList("   "));
    }

    @Test
    void createListRejectsNullName() {
        assertThrows(IllegalArgumentException.class, () -> service.createList(null));
    }

    @Test
    void listListsReturnsEmptyWhenNoLists() {
        assertTrue(service.listLists().isEmpty());
    }

    @Test
    void addTaskCreatesPendingTaskWithUniqueId() {
        long listId = inboxId();

        Task first = service.addTask(listId, "Comprar leche");
        Task second = service.addTask(listId, "Pagar facturas");

        assertNotEquals(first.getId(), second.getId());
        assertEquals(TaskStatus.PENDING, first.getStatus());
        assertEquals("Comprar leche", first.getTitle());
    }

    @Test
    void addTaskAssociatesTaskToList() {
        long inbox = inboxId();
        long work = service.createList("Trabajo").getId();

        Task task = service.addTask(inbox, "A");

        assertEquals(inbox, task.getListId());
        assertEquals(List.of(task), service.listTasks(inbox));
        assertTrue(service.listTasks(work).isEmpty());
    }

    @Test
    void addTaskThrowsWhenListDoesNotExist() {
        assertThrows(TaskListNotFoundException.class, () -> service.addTask(99, "A"));
    }

    @Test
    void addTaskRejectsEmptyTitle() {
        long listId = inboxId();
        assertThrows(IllegalArgumentException.class, () -> service.addTask(listId, ""));
    }

    @Test
    void addTaskRejectsBlankTitle() {
        long listId = inboxId();
        assertThrows(IllegalArgumentException.class, () -> service.addTask(listId, "   "));
    }

    @Test
    void addTaskRejectsNullTitle() {
        long listId = inboxId();
        assertThrows(IllegalArgumentException.class, () -> service.addTask(listId, null));
    }

    @Test
    void addTaskRejectsEmptyTitleAndDoesNotCreateTask() {
        long listId = inboxId();
        service.addTask(listId, "Única");

        assertThrows(IllegalArgumentException.class, () -> service.addTask(listId, " "));
        assertEquals(1, service.listTasks(listId).size());
    }

    @Test
    void listTasksReturnsEmptyWhenNoTasks() {
        long listId = inboxId();
        assertTrue(service.listTasks(listId).isEmpty());
    }

    @Test
    void listTasksReturnsOnlyTasksOfGivenList() {
        long inbox = inboxId();
        long work = service.createList("Trabajo").getId();
        service.addTask(inbox, "A");
        service.addTask(inbox, "B");
        service.addTask(work, "C");

        List<Task> inboxTasks = service.listTasks(inbox);
        List<Task> workTasks = service.listTasks(work);

        assertEquals(2, inboxTasks.size());
        assertEquals(1, workTasks.size());
        assertEquals("C", workTasks.get(0).getTitle());
    }

    @Test
    void completeTaskMarksExistingTaskCompleted() {
        Task task = service.addTask(inboxId(), "Comprar leche");

        service.completeTask(task.getId());

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void completeTaskThrowsWhenTaskDoesNotExist() {
        assertThrows(TaskNotFoundException.class, () -> service.completeTask(99));
    }

    @Test
    void completeTaskIsIdempotentForAlreadyCompletedTask() {
        Task task = service.addTask(inboxId(), "Comprar leche");
        service.completeTask(task.getId());

        assertDoesNotThrow(() -> service.completeTask(task.getId()));
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void reopenTaskMarksCompletedTaskPending() {
        Task task = service.addTask(inboxId(), "Comprar leche");
        service.completeTask(task.getId());

        service.reopenTask(task.getId());

        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void reopenTaskThrowsWhenTaskDoesNotExist() {
        assertThrows(TaskNotFoundException.class, () -> service.reopenTask(99));
    }

    @Test
    void reopenTaskIsIdempotentForPendingTask() {
        Task task = service.addTask(inboxId(), "Comprar leche");

        assertDoesNotThrow(() -> service.reopenTask(task.getId()));
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void deleteTaskRemovesExistingTask() {
        long listId = inboxId();
        Task task = service.addTask(listId, "Comprar leche");

        service.deleteTask(task.getId());

        assertTrue(service.listTasks(listId).isEmpty());
    }

    @Test
    void deleteTaskThrowsWhenTaskDoesNotExist() {
        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(99));
    }

    @Test
    void purgeCompletedTasksRemovesCompletedAndKeepsPending() {
        long listId = inboxId();
        Task pending = service.addTask(listId, "Pendiente");
        Task completed = service.addTask(listId, "Completada");
        service.completeTask(completed.getId());

        List<Task> removed = service.purgeCompletedTasks(listId);

        assertEquals(List.of(completed), removed);
        assertEquals(List.of(pending), service.listTasks(listId));
    }

    @Test
    void purgeCompletedTasksOnlyAffectsGivenList() {
        long inbox = inboxId();
        long work = service.createList("Trabajo").getId();
        Task inboxDone = service.addTask(inbox, "A");
        Task workDone = service.addTask(work, "B");
        service.completeTask(inboxDone.getId());
        service.completeTask(workDone.getId());

        service.purgeCompletedTasks(inbox);

        assertTrue(service.listTasks(inbox).isEmpty());
        assertEquals(List.of(workDone), service.listTasks(work));
    }

    @Test
    void purgeCompletedTasksReturnsEmptyWhenNoCompletedTasks() {
        long listId = inboxId();
        service.addTask(listId, "Pendiente");

        assertTrue(service.purgeCompletedTasks(listId).isEmpty());
        assertEquals(1, service.listTasks(listId).size());
    }

    @Test
    void completeTaskTriggersPersistence() {
        List<String> persisted = new ArrayList<>();
        TaskRepository repository = new InMemoryTaskRepository() {
            @Override
            public void persist() {
                persisted.add("persisted");
            }
        };
        TaskService service = new TaskService(repository);
        TaskList list = service.createList("Inbox");
        Task task = service.addTask(list.getId(), "Tarea");

        service.completeTask(task.getId());

        assertEquals(List.of("persisted"), persisted);
    }

    @Test
    void renameTaskUpdatesTitleAndKeepsIdAndStatus() {
        long listId = inboxId();
        Task task = service.addTask(listId, "Original");
        long id = task.getId();
        service.completeTask(task.getId());

        service.renameTask(id, "Nuevo título");

        assertEquals("Nuevo título", task.getTitle());
        assertEquals(id, task.getId());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void renameTaskRejectsBlankTitle() {
        Task task = service.addTask(inboxId(), "Original");

        assertThrows(IllegalArgumentException.class, () -> service.renameTask(task.getId(), "   "));
        assertEquals("Original", task.getTitle());
    }

    @Test
    void renameTaskRejectsNullTitle() {
        Task task = service.addTask(inboxId(), "Original");

        assertThrows(IllegalArgumentException.class, () -> service.renameTask(task.getId(), null));
        assertEquals("Original", task.getTitle());
    }

    @Test
    void renameTaskThrowsWhenTaskDoesNotExist() {
        assertThrows(TaskNotFoundException.class, () -> service.renameTask(99, "Título"));
    }

    @Test
    void renameTaskTriggersPersistence() {
        List<String> persisted = new ArrayList<>();
        TaskRepository repository = new InMemoryTaskRepository() {
            @Override
            public void persist() {
                persisted.add("persisted");
            }
        };
        TaskService service = new TaskService(repository);
        TaskList list = service.createList("Inbox");
        Task task = service.addTask(list.getId(), "Tarea");

        service.renameTask(task.getId(), "Renombrada");

        assertEquals(List.of("persisted"), persisted);
    }

    @Test
    void moveTaskMovesTaskToTargetList() {
        long inbox = inboxId();
        long work = service.createList("Trabajo").getId();
        Task task = service.addTask(inbox, "A");

        service.moveTask(task.getId(), work);

        assertEquals(work, task.getListId());
        assertTrue(service.listTasks(inbox).isEmpty());
        assertEquals(List.of(task), service.listTasks(work));
    }

    @Test
    void moveTaskThrowsWhenTaskDoesNotExist() {
        long work = service.createList("Trabajo").getId();

        assertThrows(TaskNotFoundException.class, () -> service.moveTask(99, work));
    }

    @Test
    void moveTaskThrowsWhenTargetListDoesNotExist() {
        long inbox = inboxId();
        Task task = service.addTask(inbox, "A");

        assertThrows(TaskListNotFoundException.class, () -> service.moveTask(task.getId(), 99));
        assertEquals(inbox, task.getListId());
    }

    @Test
    void moveTaskToSameListKeepsTaskUnchanged() {
        long inbox = inboxId();
        Task task = service.addTask(inbox, "A");

        assertDoesNotThrow(() -> service.moveTask(task.getId(), inbox));
        assertEquals(inbox, task.getListId());
        assertEquals(List.of(task), service.listTasks(inbox));
    }

    @Test
    void moveTaskTriggersPersistence() {
        List<String> persisted = new ArrayList<>();
        TaskRepository repository = new InMemoryTaskRepository() {
            @Override
            public void persist() {
                persisted.add("persisted");
            }
        };
        TaskService service = new TaskService(repository);
        TaskList inbox = service.createList("Inbox");
        TaskList work = service.createList("Trabajo");
        Task task = service.addTask(inbox.getId(), "Tarea");

        service.moveTask(task.getId(), work.getId());

        assertEquals(List.of("persisted"), persisted);
    }

    @Test
    void listTasksOrdersPendingBeforeCompleted() {
        long listId = inboxId();
        Task a = service.addTask(listId, "A");
        Task b = service.addTask(listId, "B");
        Task c = service.addTask(listId, "C");
        service.completeTask(b.getId());

        assertEquals(List.of(a, c, b), service.listTasks(listId));
    }

    @Test
    void listTasksKeepsRelativeOrderWithinGroups() {
        long listId = inboxId();
        Task a = service.addTask(listId, "A");
        Task b = service.addTask(listId, "B");
        Task c = service.addTask(listId, "C");
        Task d = service.addTask(listId, "D");
        service.completeTask(a.getId());
        service.completeTask(c.getId());

        assertEquals(List.of(b, d, a, c), service.listTasks(listId));
    }

    @Test
    void reopenedTaskReturnsToPendingGroup() {
        long listId = inboxId();
        Task a = service.addTask(listId, "A");
        Task b = service.addTask(listId, "B");
        service.completeTask(b.getId());

        assertEquals(List.of(a, b), service.listTasks(listId));

        service.reopenTask(b.getId());

        assertEquals(List.of(a, b), service.listTasks(listId));
    }

    @Test
    void getZoomLevelDefaultsToZero() {
        assertEquals(0, service.getZoomLevel());
    }

    @Test
    void setZoomLevelStoresValueWithinRange() {
        service.setZoomLevel(2);

        assertEquals(2, service.getZoomLevel());
    }

    @Test
    void setZoomLevelClampsAboveMax() {
        service.setZoomLevel(99);

        assertEquals(TaskService.MAX_ZOOM, service.getZoomLevel());
    }

    @Test
    void setZoomLevelClampsBelowMin() {
        service.setZoomLevel(-99);

        assertEquals(TaskService.MIN_ZOOM, service.getZoomLevel());
    }
}
