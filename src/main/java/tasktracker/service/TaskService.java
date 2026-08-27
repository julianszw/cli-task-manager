package tasktracker.service;

import java.util.List;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.model.Task;
import tasktracker.repository.TaskRepository;

public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task addTask(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        return repository.save(new Task(title));
    }

    public List<Task> listTasks() {
        return repository.findAll();
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

    public List<Task> purgeCompletedTasks() {
        return repository.removeCompleted();
    }
}
