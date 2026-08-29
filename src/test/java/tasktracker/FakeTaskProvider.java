package tasktracker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.provider.TaskProvider;

public class FakeTaskProvider implements TaskProvider {

    private final Map<String, TaskList> lists = new LinkedHashMap<>();
    private final Map<String, Task> tasks = new LinkedHashMap<>();
    private final AtomicLong listSequence = new AtomicLong();
    private final AtomicLong taskSequence = new AtomicLong();

    @Override
    public List<TaskList> listTaskLists() {
        return List.copyOf(lists.values());
    }

    @Override
    public TaskList getTaskList(String id) {
        return lists.get(id);
    }

    @Override
    public TaskList createTaskList(String title) {
        TaskList list = new TaskList(title);
        list.setId(String.valueOf(listSequence.incrementAndGet()));
        lists.put(list.getId(), list);
        return list;
    }

    @Override
    public TaskList updateTaskList(String id, String title) {
        TaskList updated = new TaskList(title);
        updated.setId(id);
        lists.put(id, updated);
        return updated;
    }

    @Override
    public void deleteTaskList(String id) {
        lists.remove(id);
    }

    @Override
    public List<Task> listTasks(String listId) {
        return tasks.values().stream()
                .filter(task -> listId.equals(task.getListId()))
                .toList();
    }

    @Override
    public Task getTask(String listId, String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public Task createTask(String listId, String title, String due) {
        Task task = new Task(title);
        task.setId(String.valueOf(taskSequence.incrementAndGet()));
        task.setListId(listId);
        task.setDue(due);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        return task;
    }

    @Override
    public void deleteTask(String listId, String taskId) {
        tasks.remove(taskId);
    }

    @Override
    public Task moveTask(String taskListId, String taskId, String destinationListId) {
        Task task = tasks.get(taskId);
        task.setListId(destinationListId);
        return task;
    }

    @Override
    public String providerName() {
        return "Fake";
    }

    @Override
    public void clearTasks(String listId) {
        tasks.values().removeIf(task -> listId.equals(task.getListId()));
    }
}
