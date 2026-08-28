package tasktracker.provider;

import java.util.List;
import tasktracker.model.Task;
import tasktracker.model.TaskList;

public interface TaskProvider {

    List<TaskList> listTaskLists();

    TaskList getTaskList(String id);

    TaskList createTaskList(String title);

    TaskList updateTaskList(String id, String title);

    void deleteTaskList(String id);

    List<Task> listTasks(String listId);

    Task getTask(String listId, String taskId);

    Task createTask(String listId, String title, String due);

    Task updateTask(Task task);

    void deleteTask(String listId, String taskId);

    Task moveTask(String taskListId, String taskId, String destinationListId);

    void clearTasks(String listId);
}
