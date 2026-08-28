package tasktracker.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.io.IOException;
import java.util.List;
import tasktracker.sync.GoogleTasksClient;
import tasktracker.sync.RemoteTask;
import tasktracker.sync.RemoteTaskList;
import tasktracker.sync.SyncException;

public final class HttpGoogleTasksClient implements GoogleTasksClient {

    private static final String APPLICATION_NAME = "cli-task-tracker";

    private final GoogleAuth auth;

    public HttpGoogleTasksClient(GoogleAuth auth) {
        this.auth = auth;
    }

    @Override
    public boolean isAuthenticated() {
        return auth.hasStoredCredentials();
    }

    @Override
    public void authorize() {
        auth.authorize();
    }

    @Override
    public List<RemoteTaskList> listTaskLists() {
        try {
            List<TaskList> items = service().tasklists().list()
                    .setMaxResults(100).execute().getItems();
            if (items == null) {
                return List.of();
            }
            return items.stream()
                    .map(list -> new RemoteTaskList(list.getId(), list.getTitle(), toMillis(list.getUpdated())))
                    .toList();
        } catch (IOException e) {
            throw handleException("No se pudieron listar las listas", e);
        }
    }

    @Override
    public List<RemoteTask> listTasks(String remoteListId) {
        try {
            List<Task> items = service().tasks().list(remoteListId)
                    .setMaxResults(100).execute().getItems();
            if (items == null) {
                return List.of();
            }
            return items.stream()
                    .map(task -> toRemoteTask(task, remoteListId))
                    .toList();
        } catch (IOException e) {
            throw handleException("No se pudieron listar las tareas", e);
        }
    }

    @Override
    public RemoteTaskList createTaskList(String title) {
        try {
            TaskList created = service().tasklists().insert(new TaskList().setTitle(title)).execute();
            return new RemoteTaskList(created.getId(), created.getTitle(), toMillis(created.getUpdated()));
        } catch (IOException e) {
            throw handleException("No se pudo crear la lista", e);
        }
    }

    @Override
    public RemoteTask createTask(String remoteListId, String title) {
        try {
            Task created = service().tasks().insert(remoteListId, new Task().setTitle(title)).execute();
            return toRemoteTask(created, remoteListId);
        } catch (IOException e) {
            throw handleException("No se pudo crear la tarea", e);
        }
    }

    @Override
    public RemoteTask updateTask(String remoteListId, String taskId, String title, boolean completed) {
        try {
            Task updated = service().tasks().update(remoteListId, taskId,
                    new Task().setId(taskId).setTitle(title).setStatus(completed ? "completed" : "needsAction")).execute();
            return toRemoteTask(updated, remoteListId);
        } catch (IOException e) {
            throw handleException("No se pudo actualizar la tarea", e);
        }
    }

    @Override
    public void deleteTask(String remoteListId, String taskId) {
        try {
            service().tasks().delete(remoteListId, taskId).execute();
        } catch (IOException e) {
            throw handleException("No se pudo eliminar la tarea", e);
        }
    }

    private Tasks service() {
        try {
            return new Tasks.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    auth.loadCredential())
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            throw new SyncException("No se pudo construir el cliente de Google Tasks: " + e.getMessage(), e);
        }
    }

    private static RemoteTask toRemoteTask(Task task, String remoteListId) {
        boolean completed = "completed".equals(task.getStatus());
        return new RemoteTask(task.getId(), task.getTitle(), completed, remoteListId, toMillis(task.getUpdated()));
    }

    private static long toMillis(String updated) {
        if (updated == null || updated.isBlank()) {
            return 0L;
        }
        try {
            return DateTime.parseRfc3339(updated).getValue();
        } catch (RuntimeException e) {
            return 0L;
        }
    }

    private SyncException handleException(String message, IOException e) {
        if (e instanceof GoogleJsonResponseException ge && ge.getDetails() != null) {
            return new SyncException(message + ": " + ge.getDetails().getMessage(), e);
        }
        return new SyncException(message + ": " + e.getMessage(), e);
    }
}
