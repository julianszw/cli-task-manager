package tasktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

class JsonTaskRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void startsEmptyWhenFileDoesNotExistAndCreatesFileOnFirstSave() throws IOException {
        Path file = tempDir.resolve("tasks.json");

        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        assertTrue(repository.findAll().isEmpty());
        assertTrue(Files.notExists(file));

        repository.save(new Task("Primera"));

        assertTrue(Files.exists(file));
    }

    @Test
    void loadsTasksFromExistingFile() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "[{\"id\":3,\"title\":\"Comprar leche\",\"status\":\"PENDING\"},"
                + "{\"id\":7,\"title\":\"Pagar\",\"status\":\"COMPLETED\"}]");

        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        List<Task> tasks = repository.findAll();
        assertEquals(2, tasks.size());
        assertEquals(3, tasks.get(0).getId());
        assertEquals("Comprar leche", tasks.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus());
        assertEquals(7, tasks.get(1).getId());
        assertEquals(TaskStatus.COMPLETED, tasks.get(1).getStatus());
    }

    @Test
    void startsEmptyAndWarnsWhenFileIsCorrupt() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "{ esto no es un json válido");

        List<String> warnings = new ArrayList<>();
        JsonTaskRepository repository = new JsonTaskRepository(file, warnings::add);

        assertTrue(repository.findAll().isEmpty());
        assertFalse(warnings.isEmpty());
        assertTrue(warnings.stream().anyMatch(w -> w.contains("cargar")));
    }

    @Test
    void savesPersistToFileAfterEachMutation() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        repository.save(new Task("Primera"));
        repository.save(new Task("Segunda"));

        List<Task> decoded = JsonTasksCodec.decode(Files.readString(file));
        assertEquals(2, decoded.size());
        assertEquals("Primera", decoded.get(0).getTitle());
        assertEquals("Segunda", decoded.get(1).getTitle());
    }

    @Test
    void assignsIdsGreaterThanMaxLoadedId() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "[{\"id\":5,\"title\":\"A\",\"status\":\"PENDING\"},"
                + "{\"id\":10,\"title\":\"B\",\"status\":\"PENDING\"}]");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        Task created = repository.save(new Task("Nueva"));

        assertEquals(11, created.getId());
    }

    @Test
    void assignsInitialIdWhenNoTasksLoaded() {
        JsonTaskRepository repository = new JsonTaskRepository(tempDir.resolve("tasks.json"), message -> {
        });

        Task created = repository.save(new Task("Única"));

        assertEquals(1, created.getId());
    }

    @Test
    void removeByIdPersists() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });
        Task task = repository.save(new Task("Eliminar"));

        repository.removeById(task.getId());

        assertTrue(JsonTasksCodec.decode(Files.readString(file)).isEmpty());
    }

    @Test
    void removeCompletedPersists() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });
        repository.save(new Task("Pendiente"));
        Task completed = repository.save(new Task("Completada"));
        completed.markCompleted();

        repository.removeCompleted();

        List<Task> decoded = JsonTasksCodec.decode(Files.readString(file));
        assertEquals(1, decoded.size());
        assertEquals("Pendiente", decoded.get(0).getTitle());
    }

    @Test
    void readOperationsDoNotModifyFile() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });
        repository.save(new Task("A"));

        String before = Files.readString(file);
        repository.findAll();
        repository.findById(1L);

        assertEquals(before, Files.readString(file));
    }

    @Test
    void writeFailureKeepsTasksInMemoryAndWarns() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        List<String> warnings = new ArrayList<>();
        JsonTaskRepository repository = new JsonTaskRepository(file, warnings::add);

        Files.createDirectory(file);

        Task task = repository.save(new Task("Importante"));

        assertEquals(1, repository.findAll().size());
        assertEquals("Importante", task.getTitle());
        assertTrue(warnings.stream().anyMatch(w -> w.contains("guardar")));
    }
}
