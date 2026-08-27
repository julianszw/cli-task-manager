package tasktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskList;

class InMemoryTaskRepositoryTest {

    @Test
    void saveAssignsUniqueIncrementalIds() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        Task first = repository.save(new Task("Comprar leche"));
        Task second = repository.save(new Task("Pagar facturas"));

        assertNotEquals(first.getId(), second.getId());
        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void findAllReturnsEmptyWhenNoTasks() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAllReturnsAllSavedTasksInInsertionOrder() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        Task first = repository.save(new Task("A"));
        Task second = repository.save(new Task("B"));

        List<Task> tasks = repository.findAll();

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void findByIdReturnsTaskWhenPresent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        Task task = repository.save(new Task("A"));

        Optional<Task> found = repository.findById(task.getId());

        assertTrue(found.isPresent());
        assertEquals(task, found.get());
    }

    @Test
    void findByIdReturnsEmptyWhenAbsent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        assertTrue(repository.findById(99).isEmpty());
    }

    @Test
    void removeByIdRemovesAndReturnsTask() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        Task task = repository.save(new Task("A"));

        Optional<Task> removed = repository.removeById(task.getId());

        assertTrue(removed.isPresent());
        assertEquals(task, removed.get());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void removeByIdReturnsEmptyWhenAbsent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        assertTrue(repository.removeById(99).isEmpty());
    }

    @Test
    void saveListAssignsUniqueIncrementalIds() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        TaskList first = repository.saveList(new TaskList("Inbox"));
        TaskList second = repository.saveList(new TaskList("Trabajo"));

        assertNotEquals(first.getId(), second.getId());
        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void findAllListsReturnsEmptyWhenNoLists() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        assertTrue(repository.findAllLists().isEmpty());
    }

    @Test
    void findAllListsReturnsListsInInsertionOrder() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskList first = repository.saveList(new TaskList("Inbox"));
        TaskList second = repository.saveList(new TaskList("Trabajo"));

        List<TaskList> lists = repository.findAllLists();

        assertEquals(List.of(first, second), lists);
    }

    @Test
    void findListByIdReturnsListWhenPresent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskList list = repository.saveList(new TaskList("Inbox"));

        Optional<TaskList> found = repository.findListById(list.getId());

        assertTrue(found.isPresent());
        assertEquals(list, found.get());
    }

    @Test
    void findListByIdReturnsEmptyWhenAbsent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        assertTrue(repository.findListById(99).isEmpty());
    }

    @Test
    void removeCompletedRemovesOnlyCompletedTasksOfList() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        Task pending = repository.save(new Task("Pendiente"));
        pending.setListId(1);
        Task completed = repository.save(new Task("Completada"));
        completed.setListId(1);
        completed.markCompleted();
        Task otherCompleted = repository.save(new Task("Otra lista"));
        otherCompleted.setListId(2);
        otherCompleted.markCompleted();

        List<Task> removed = repository.removeCompleted(1);

        assertEquals(List.of(completed), removed);
        assertEquals(List.of(pending, otherCompleted), repository.findAll());
    }

    @Test
    void removeCompletedReturnsEmptyWhenNoCompletedTasks() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        Task pending = repository.save(new Task("Pendiente"));
        pending.setListId(1);

        assertTrue(repository.removeCompleted(1).isEmpty());
        assertEquals(1, repository.findAll().size());
    }
}
