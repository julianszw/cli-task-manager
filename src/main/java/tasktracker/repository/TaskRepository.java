package tasktracker.repository;

import java.util.List;
import java.util.Optional;
import tasktracker.model.Task;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();

    Optional<Task> findById(long id);

    Optional<Task> removeById(long id);

    List<Task> removeCompleted();
}
