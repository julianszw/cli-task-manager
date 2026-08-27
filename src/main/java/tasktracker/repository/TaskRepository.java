package tasktracker.repository;

import java.util.List;
import java.util.Optional;
import tasktracker.model.Task;
import tasktracker.model.TaskList;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();

    Optional<Task> findById(long id);

    Optional<Task> removeById(long id);

    List<Task> removeCompleted(long listId);

    List<TaskList> findAllLists();

    TaskList saveList(TaskList list);

    Optional<TaskList> findListById(long id);

    void persist();
}
