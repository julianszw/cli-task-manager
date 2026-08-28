package tasktracker.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import java.io.IOException;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.provider.ProviderException;
import tasktracker.provider.TaskProvider;

public final class GoogleTasksProvider implements TaskProvider {

    private static final String APPLICATION_NAME = "cli-task-tracker";

    private final GoogleAuth auth;

    public GoogleTasksProvider(GoogleAuth auth) {
        this.auth = auth;
    }

    @Override
    public List<TaskList> listTaskLists() {
        try {
            List<com.google.api.services.tasks.model.TaskList> items =
                    service().tasklists().list().setMaxResults(100).execute().getItems();
            if (items == null) {
                return List.of();
            }
            return items.stream().map(GoogleTasksProvider::toTaskList).toList();
        } catch (IOException e) {
            throw handleException("No se pudieron listar las listas", e);
        }
    }

    @Override
    public TaskList getTaskList(String id) {
        try {
            return toTaskList(service().tasklists().get(id).execute());
        } catch (IOException e) {
            throw handleException("No se pudo obtener la lista", e);
        }
    }

    @Override
    public TaskList createTaskList(String title) {
        try {
            com.google.api.services.tasks.model.TaskList created = service().tasklists()
                    .insert(new com.google.api.services.tasks.model.TaskList().setTitle(title))
                    .execute();
            return toTaskList(created);
        } catch (IOException e) {
            throw handleException("No se pudo crear la lista", e);
        }
    }

    @Override
    public TaskList updateTaskList(String id, String title) {
        try {
            com.google.api.services.tasks.model.TaskList updated = service().tasklists()
                    .update(id, new com.google.api.services.tasks.model.TaskList().setId(id).setTitle(title))
                    .execute();
            return toTaskList(updated);
        } catch (IOException e) {
            throw handleException("No se pudo actualizar la lista", e);
        }
    }

    @Override
    public void deleteTaskList(String id) {
        try {
            service().tasklists().delete(id).execute();
        } catch (IOException e) {
            throw handleException("No se pudo eliminar la lista", e);
        }
    }

    @Override
    public List<Task> listTasks(String listId) {
        try {
            List<com.google.api.services.tasks.model.Task> items =
                    service().tasks().list(listId).setMaxResults(100).execute().getItems();
            if (items == null) {
                return List.of();
            }
            return items.stream().map(task -> toTask(task, listId)).toList();
        } catch (IOException e) {
            throw handleException("No se pudieron listar las tareas", e);
        }
    }

    @Override
    public Task getTask(String listId, String taskId) {
        try {
            return toTask(service().tasks().get(listId, taskId).execute(), listId);
        } catch (IOException e) {
            throw handleException("No se pudo obtener la tarea", e);
        }
    }

    @Override
    public Task createTask(String listId, String title, String due) {
        try {
            com.google.api.services.tasks.model.Task googleTask =
                    new com.google.api.services.tasks.model.Task().setTitle(title);
            if (due != null) {
                googleTask.setDue(due);
            }
            return toTask(service().tasks().insert(listId, googleTask).execute(), listId);
        } catch (IOException e) {
            throw handleException("No se pudo crear la tarea", e);
        }
    }

    @Override
    public Task updateTask(Task task) {
        try {
            return toTask(service().tasks().update(task.getListId(), task.getId(), toGoogleTask(task)).execute(),
                    task.getListId());
        } catch (IOException e) {
            throw handleException("No se pudo actualizar la tarea", e);
        }
    }

    @Override
    public void deleteTask(String listId, String taskId) {
        try {
            service().tasks().delete(listId, taskId).execute();
        } catch (IOException e) {
            throw handleException("No se pudo eliminar la tarea", e);
        }
    }

    @Override
    public Task moveTask(String taskListId, String taskId, String destinationListId) {
        try {
            com.google.api.services.tasks.model.Task moved = service().tasks().move(taskListId, taskId)
                    .setDestinationTasklist(destinationListId)
                    .execute();
            return toTask(moved, destinationListId);
        } catch (IOException e) {
            throw handleException("No se pudo mover la tarea", e);
        }
    }

    @Override
    public void clearTasks(String listId) {
        try {
            service().tasks().clear(listId).execute();
        } catch (IOException e) {
            throw handleException("No se pudieron eliminar las tareas", e);
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
            throw new ProviderException("No se pudo construir el cliente de Google Tasks: " + e.getMessage(), e);
        }
    }

    private static Task toTask(com.google.api.services.tasks.model.Task googleTask, String listId) {
        Task task = new Task(googleTask.getTitle());
        task.setId(googleTask.getId());
        task.setListId(listId);
        task.setDue(googleTask.getDue());
        if ("completed".equals(googleTask.getStatus())) {
            task.markCompleted();
        }
        return task;
    }

    private static com.google.api.services.tasks.model.Task toGoogleTask(Task task) {
        com.google.api.services.tasks.model.Task googleTask = new com.google.api.services.tasks.model.Task()
                .setId(task.getId())
                .setTitle(task.getTitle())
                .setStatus(task.isCompleted() ? "completed" : "needsAction");
        if (task.getDue() != null) {
            googleTask.setDue(task.getDue());
        }
        return googleTask;
    }

    private static TaskList toTaskList(com.google.api.services.tasks.model.TaskList list) {
        TaskList taskList = new TaskList(list.getTitle());
        taskList.setId(list.getId());
        return taskList;
    }

    private ProviderException handleException(String message, IOException e) {
        if (e instanceof GoogleJsonResponseException ge && ge.getDetails() != null) {
            return new ProviderException(message + ": " + ge.getDetails().getMessage(), e);
        }
        return new ProviderException(message + ": " + e.getMessage(), e);
    }
}
