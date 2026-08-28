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
import tasktracker.model.TaskList;
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
        assertTrue(repository.findAllLists().isEmpty());
        assertTrue(Files.notExists(file));

        repository.saveList(new TaskList("Inbox"));

        assertTrue(Files.exists(file));
    }

    @Test
    void loadsListsAndTasksFromExistingFile() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "{\"lists\":[{\"id\":1,\"name\":\"Inbox\"},{\"id\":5,\"name\":\"Trabajo\"}],"
                + "\"tasks\":[{\"id\":3,\"title\":\"Comprar leche\",\"status\":\"PENDING\",\"listId\":1},"
                + "{\"id\":7,\"title\":\"Pagar\",\"status\":\"COMPLETED\",\"listId\":5}]}");

        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        List<TaskList> lists = repository.findAllLists();
        List<Task> tasks = repository.findAll();
        assertEquals(2, lists.size());
        assertEquals("Inbox", lists.get(0).getName());
        assertEquals("Trabajo", lists.get(1).getName());
        assertEquals(2, tasks.size());
        assertEquals(3, tasks.get(0).getId());
        assertEquals("Comprar leche", tasks.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus());
        assertEquals(1, tasks.get(0).getListId());
        assertEquals(7, tasks.get(1).getId());
        assertEquals(TaskStatus.COMPLETED, tasks.get(1).getStatus());
    }

    @Test
    void legacyFlatArrayMigratesToInbox() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "[{\"id\":3,\"title\":\"A\",\"status\":\"PENDING\"}]");

        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        List<TaskList> lists = repository.findAllLists();
        assertEquals(1, lists.size());
        assertEquals("Inbox", lists.get(0).getName());
        assertEquals(1, repository.findAll().size());
        assertEquals(1, repository.findAll().get(0).getListId());
    }

    @Test
    void startsEmptyAndWarnsWhenFileIsCorrupt() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "{ esto no es un json válido");

        List<String> warnings = new ArrayList<>();
        JsonTaskRepository repository = new JsonTaskRepository(file, warnings::add);

        assertTrue(repository.findAll().isEmpty());
        assertTrue(repository.findAllLists().isEmpty());
        assertFalse(warnings.isEmpty());
        assertTrue(warnings.stream().anyMatch(w -> w.contains("cargar")));
    }

    @Test
    void savesPersistToFileAfterEachMutation() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        TaskList inbox = repository.saveList(new TaskList("Inbox"));
        repository.save(task(inbox.getId(), "Primera"));
        repository.save(task(inbox.getId(), "Segunda"));

        JsonStoreCodec.Store decoded = JsonStoreCodec.decode(Files.readString(file));
        assertEquals(1, decoded.lists().size());
        assertEquals(2, decoded.tasks().size());
        assertEquals("Primera", decoded.tasks().get(0).getTitle());
        assertEquals("Segunda", decoded.tasks().get(1).getTitle());
    }

    @Test
    void assignsTaskIdsGreaterThanMaxLoadedId() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "{\"lists\":[{\"id\":1,\"name\":\"Inbox\"}],"
                + "\"tasks\":[{\"id\":5,\"title\":\"A\",\"status\":\"PENDING\",\"listId\":1},"
                + "{\"id\":10,\"title\":\"B\",\"status\":\"PENDING\",\"listId\":1}]}");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        Task created = repository.save(task(1, "Nueva"));

        assertEquals(11, created.getId());
    }

    @Test
    void assignsInitialTaskIdWhenNoTasksLoaded() {
        JsonTaskRepository repository = new JsonTaskRepository(tempDir.resolve("tasks.json"), message -> {
        });

        Task created = repository.save(task(0, "Única"));

        assertEquals(1, created.getId());
    }

    @Test
    void assignsListIdsGreaterThanMaxLoadedListId() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "{\"lists\":[{\"id\":5,\"name\":\"Inbox\"}],\"tasks\":[]}");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        TaskList created = repository.saveList(new TaskList("Trabajo"));

        assertEquals(6, created.getId());
    }

    @Test
    void removeByIdPersists() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });
        TaskList inbox = repository.saveList(new TaskList("Inbox"));
        Task task = repository.save(task(inbox.getId(), "Eliminar"));

        repository.removeById(task.getId());

        assertTrue(JsonStoreCodec.decode(Files.readString(file)).tasks().isEmpty());
    }

    @Test
    void removeCompletedPersists() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });
        TaskList inbox = repository.saveList(new TaskList("Inbox"));
        repository.save(task(inbox.getId(), "Pendiente"));
        Task completed = repository.save(task(inbox.getId(), "Completada"));
        completed.markCompleted();

        repository.removeCompleted(inbox.getId());

        List<Task> decoded = JsonStoreCodec.decode(Files.readString(file)).tasks();
        assertEquals(1, decoded.size());
        assertEquals("Pendiente", decoded.get(0).getTitle());
    }

    @Test
    void readOperationsDoNotModifyFile() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });
        TaskList inbox = repository.saveList(new TaskList("Inbox"));
        repository.save(task(inbox.getId(), "A"));

        String before = Files.readString(file);
        repository.findAll();
        repository.findById(1L);
        repository.findAllLists();
        repository.findListById(inbox.getId());

        assertEquals(before, Files.readString(file));
    }

    @Test
    void writeFailureKeepsListsInMemoryAndWarns() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        List<String> warnings = new ArrayList<>();
        JsonTaskRepository repository = new JsonTaskRepository(file, warnings::add);

        Files.createDirectory(file);

        TaskList list = repository.saveList(new TaskList("Importante"));

        assertEquals(1, repository.findAllLists().size());
        assertEquals("Importante", list.getName());
        assertTrue(warnings.stream().anyMatch(w -> w.contains("guardar")));
    }

    @Test
    void setZoomPersistsAndReloads() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        repository.setZoom(2);

        assertEquals("2", String.valueOf(JsonStoreCodec.decode(Files.readString(file)).zoom()));

        JsonTaskRepository reloaded = new JsonTaskRepository(file, message -> {
        });
        assertEquals(2, reloaded.getZoom());
    }

    @Test
    void loadsZoomFromExistingFile() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "{\"lists\":[],\"tasks\":[],\"zoom\":-2}");

        JsonTaskRepository repository = new JsonTaskRepository(file, message -> {
        });

        assertEquals(-2, repository.getZoom());
    }

    private static Task task(long listId, String title) {
        Task task = new Task(title);
        task.setListId(listId);
        return task;
    }
}
