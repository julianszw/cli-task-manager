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

public class JsonTaskRepository implements TaskRepository {

    private final Path file;
    private final Consumer<String> warningConsumer;
    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

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
    public List<Task> removeCompleted() {
        List<Task> removed = tasks.values().stream()
                .filter(Task::isCompleted)
                .toList();
        removed.forEach(task -> tasks.remove(task.getId()));
        if (!removed.isEmpty()) {
            persist();
        }
        return removed;
    }

    @Override
    public void persist() {
        String json = JsonTasksCodec.encode(findAll());
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
            List<Task> loaded = JsonTasksCodec.decode(Files.readString(file));
            long maxId = 0;
            for (Task task : loaded) {
                tasks.put(task.getId(), task);
                maxId = Math.max(maxId, task.getId());
            }
            sequence.set(maxId);
        } catch (IOException | JsonParseException e) {
            warningConsumer.accept("No se pudo cargar el archivo de tareas: " + e.getMessage());
        }
    }
}
