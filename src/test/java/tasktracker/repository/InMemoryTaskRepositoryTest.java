package tasktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;

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
    void removeCompletedRemovesOnlyCompletedTasks() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        Task pending = repository.save(new Task("Pendiente"));
        Task completed = repository.save(new Task("Completada"));
        completed.markCompleted();

        List<Task> removed = repository.removeCompleted();

        assertEquals(List.of(completed), removed);
        assertEquals(List.of(pending), repository.findAll());
    }

    @Test
    void removeCompletedReturnsEmptyWhenNoCompletedTasks() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        repository.save(new Task("Pendiente"));

        assertTrue(repository.removeCompleted().isEmpty());
        assertEquals(1, repository.findAll().size());
    }
}
