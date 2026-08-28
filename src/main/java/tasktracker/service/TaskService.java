package tasktracker.service;

import java.util.Comparator;
import java.util.List;
import tasktracker.exception.TaskListNotFoundException;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.repository.TaskRepository;

public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskList> listLists() {
        return repository.findAllLists();
    }

    public TaskList createList(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
        }
        return repository.saveList(new TaskList(name));
    }

    public Task addTask(long listId, String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        repository.findListById(listId)
                .orElseThrow(() -> new TaskListNotFoundException(listId));
        Task task = new Task(title);
        task.setListId(listId);
        return repository.save(task);
    }

    public List<Task> listTasks(long listId) {
        return repository.findAll().stream()
                .filter(task -> task.getListId() == listId)
                .sorted(Comparator.comparing(Task::isCompleted))
                .toList();
    }

    public void completeTask(long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.markCompleted();
        repository.persist();
    }

    public void reopenTask(long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.markPending();
        repository.persist();
    }

    public void renameTask(long id, String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.rename(title);
        repository.persist();
    }

    public void deleteTask(long id) {
        repository.removeById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public void moveTask(long taskId, long targetListId) {
        repository.findListById(targetListId)
                .orElseThrow(() -> new TaskListNotFoundException(targetListId));
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (task.getListId() == targetListId) {
            return;
        }
        task.setListId(targetListId);
        repository.persist();
    }

    public List<Task> purgeCompletedTasks(long listId) {
        return repository.removeCompleted(listId);
    }
}
