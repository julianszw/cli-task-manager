package tasktracker.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import tasktracker.model.Task;

public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Task save(Task task) {
        long id = sequence.incrementAndGet();
        task.setId(id);
        tasks.put(id, task);
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
}
