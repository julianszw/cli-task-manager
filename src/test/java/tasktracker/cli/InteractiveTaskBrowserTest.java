package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class InteractiveTaskBrowserTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());

    @Test
    void emptyListShowsMessageAndWaitsForExit() {
        FakeKeySource source = new FakeKeySource(Key.EXIT);

        String output = runBrowser(source);

        assertTrue(output.contains("No hay tareas cargadas"));
    }

    @Test
    void exitRendersTableWithHeader() {
        service.addTask("A");
        FakeKeySource source = new FakeKeySource(Key.EXIT);

        String output = runBrowser(source);

        assertTrue(output.contains("ID"));
        assertTrue(output.contains("ESTADO"));
        assertTrue(output.contains("TÍTULO"));
    }

    @Test
    void completeSelectedTask() {
        service.addTask("A");
        service.addTask("B");
        FakeKeySource source = new FakeKeySource(Key.COMPLETE, Key.EXIT);

        runBrowser(source);

        List<Task> tasks = service.listTasks();
        assertEquals(TaskStatus.COMPLETED, tasks.get(0).getStatus());
        assertEquals(TaskStatus.PENDING, tasks.get(1).getStatus());
    }

    @Test
    void moveDownThenCompleteSelectsSecondTask() {
        service.addTask("A");
        service.addTask("B");
        FakeKeySource source = new FakeKeySource(Key.DOWN, Key.COMPLETE, Key.EXIT);

        runBrowser(source);

        List<Task> tasks = service.listTasks();
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus());
        assertEquals(TaskStatus.COMPLETED, tasks.get(1).getStatus());
    }

    @Test
    void deleteSelectedTask() {
        service.addTask("A");
        service.addTask("B");
        FakeKeySource source = new FakeKeySource(Key.DELETE, Key.EXIT);

        runBrowser(source);

        List<Task> tasks = service.listTasks();
        assertEquals(1, tasks.size());
        assertEquals("B", tasks.get(0).getTitle());
    }

    @Test
    void purgeRemovesCompletedTasks() {
        service.addTask("Pendiente");
        Task completed = service.addTask("Completada");
        service.completeTask(completed.getId());
        FakeKeySource source = new FakeKeySource(Key.PURGE, Key.EXIT);

        runBrowser(source);

        List<Task> tasks = service.listTasks();
        assertEquals(1, tasks.size());
        assertEquals("Pendiente", tasks.get(0).getTitle());
    }

    @Test
    void purgeWithoutCompletedShowsMessage() {
        service.addTask("Pendiente");
        FakeKeySource source = new FakeKeySource(Key.PURGE, Key.EXIT);

        String output = runBrowser(source);

        assertTrue(output.contains("No hay tareas completadas para eliminar"));
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void helpLineIsAlwaysVisible() {
        service.addTask("A");
        FakeKeySource source = new FakeKeySource(Key.EXIT);

        String output = runBrowser(source);

        assertTrue(output.contains("Teclas:"));
        assertTrue(output.contains("b atrás"));
        assertTrue(output.contains("q/Esc salir"));
    }

    @Test
    void backKeyExitsBrowser() {
        service.addTask("A");
        FakeKeySource source = new FakeKeySource(Key.BACK);

        String output = runBrowser(source);

        assertTrue(output.contains("ID"));
        assertEquals(1, service.listTasks().size());
    }

    private String runBrowser(FakeKeySource source) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            new InteractiveTaskBrowser(service, System.out).run(source);
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }

    private static class FakeKeySource implements KeySource {

        private final Queue<Key> keys = new ArrayDeque<>();

        FakeKeySource(Key... keys) {
            Collections.addAll(this.keys, keys);
        }

        @Override
        public Key readKey() {
            return keys.isEmpty() ? Key.EXIT : keys.remove();
        }
    }
}
