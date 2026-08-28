package tasktracker.sync;

import java.util.List;

public interface GoogleTasksClient {

    boolean isAuthenticated();

    void authorize();

    List<RemoteTaskList> listTaskLists();

    List<RemoteTask> listTasks(String remoteListId);

    RemoteTaskList createTaskList(String title);

    RemoteTask createTask(String remoteListId, String title);

    RemoteTask updateTask(String remoteListId, String taskId, String title, boolean completed);

    void deleteTask(String remoteListId, String taskId);
}
