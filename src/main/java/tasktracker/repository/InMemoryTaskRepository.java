package tasktracker.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import tasktracker.model.Task;
import tasktracker.model.TaskList;

public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private final Map<Long, TaskList> lists = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicLong listSequence = new AtomicLong();
    private int zoom;

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

    @Override
    public Optional<Task> removeById(long id) {
        return Optional.ofNullable(tasks.remove(id));
    }

    @Override
    public List<Task> removeCompleted(long listId) {
        List<Task> removed = tasks.values().stream()
                .filter(t -> t.isCompleted() && t.getListId() == listId)
                .toList();
        removed.forEach(task -> tasks.remove(task.getId()));
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
        return list;
    }

    @Override
    public Optional<TaskList> findListById(long id) {
        return Optional.ofNullable(lists.get(id));
    }

    @Override
    public int getZoom() {
        return zoom;
    }

    @Override
    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    @Override
    public void persist() {
    }
}
