package tasktracker.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import tasktracker.model.Task;
import tasktracker.model.TaskList;

public class JsonTaskRepository implements TaskRepository {

    private final Path file;
    private final Consumer<String> warningConsumer;
    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private final Map<Long, TaskList> lists = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicLong listSequence = new AtomicLong();

    public JsonTaskRepository(Path file, Consumer<String> warningConsumer) {
        this.file = file;
        this.warningConsumer = warningConsumer;
        load();
    }

    @Override
    public Task save(Task task) {
        long id = sequence.incrementAndGet();
        task.setId(id);
        tasks.put(id, task);
        persist();
        return task;
    }

    @Override
    public List<Task> findAll() {
        return List.copyOf(tasks.values());
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<Task> removeById(long id) {
        Optional<Task> removed = Optional.ofNullable(tasks.remove(id));
        if (removed.isPresent()) {
            persist();
        }
        return removed;
    }

    @Override
    public List<Task> removeCompleted(long listId) {
        List<Task> removed = tasks.values().stream()
                .filter(t -> t.isCompleted() && t.getListId() == listId)
                .toList();
        removed.forEach(task -> tasks.remove(task.getId()));
        if (!removed.isEmpty()) {
            persist();
        }
        return removed;
    }

    @Override
    public List<TaskList> findAllLists() {
        return List.copyOf(lists.values());
    }

    @Override
    public TaskList saveList(TaskList list) {
        long id = listSequence.incrementAndGet();
        list.setId(id);
        lists.put(id, list);
        persist();
        return list;
    }

    @Override
    public Optional<TaskList> findListById(long id) {
        return Optional.ofNullable(lists.get(id));
    }

    @Override
    public void persist() {
        String json = JsonStoreCodec.encode(findAllLists(), findAll());
        try {
            Files.writeString(file, json);
        } catch (IOException e) {
            warningConsumer.accept("No se pudo guardar el archivo de tareas: " + e.getMessage());
        }
    }

    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        try {
            JsonStoreCodec.Store store = JsonStoreCodec.decode(Files.readString(file));
            long maxTaskId = 0;
            for (Task task : store.tasks()) {
                tasks.put(task.getId(), task);
                maxTaskId = Math.max(maxTaskId, task.getId());
            }
            long maxListId = 0;
            for (TaskList list : store.lists()) {
                lists.put(list.getId(), list);
                maxListId = Math.max(maxListId, list.getId());
            }
            sequence.set(maxTaskId);
            listSequence.set(maxListId);
        } catch (IOException | JsonParseException e) {
            warningConsumer.accept("No se pudo cargar el archivo de tareas: " + e.getMessage());
        }
    }
}
