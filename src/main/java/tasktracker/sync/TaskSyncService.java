package tasktracker.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.repository.TaskRepository;

public final class TaskSyncService {

    private final TaskRepository repository;
    private final GoogleTasksClient client;
    private final SyncStateStore stateStore;

    public TaskSyncService(TaskRepository repository, GoogleTasksClient client, SyncStateStore stateStore) {
        this.repository = repository;
        this.client = client;
        this.stateStore = stateStore;
    }

    public void ensureAuthenticated() {
        if (!client.isAuthenticated()) {
            client.authorize();
        }
    }

    public boolean isAuthenticated() {
        return client.isAuthenticated();
    }

    public SyncResult sync() {
        SyncState state = stateStore.load();
        Counts counts = new Counts();

        syncLists(counts);

        Map<String, Long> remoteToLocalList = remoteToLocalListMap();
        Set<String> knownTaskIds = new HashSet<>(state.getTaskIds());
        List<Task> localTasks = new ArrayList<>(repository.findAll());

        Map<String, Task> localByRemoteId = new HashMap<>();
        for (Task task : localTasks) {
            if (task.getRemoteId() != null) {
                localByRemoteId.put(task.getRemoteId(), task);
            }
        }

        List<RemoteTask> remoteTasks = loadRemoteTasks();
        Map<String, RemoteTask> remoteById = new HashMap<>();
        for (RemoteTask remote : remoteTasks) {
            remoteById.put(remote.id(), remote);
        }

        Set<String> deletedLocally = new HashSet<>(knownTaskIds);
        deletedLocally.removeAll(localByRemoteId.keySet());

        for (String remoteId : deletedLocally) {
            RemoteTask remote = remoteById.get(remoteId);
            if (remote != null) {
                client.deleteTask(remote.listId(), remote.id());
                counts.deletedRemote++;
            }
        }

        for (Task local : localTasks) {
            if (local.getRemoteId() == null) {
                continue;
            }
            RemoteTask remote = remoteById.get(local.getRemoteId());
            if (remote == null) {
                repository.removeById(local.getId());
                counts.deletedLocal++;
            } else {
                reconcileTask(local, remote, counts);
            }
        }

        for (Task local : localTasks) {
            if (local.getRemoteId() != null) {
                continue;
            }
            String remoteListId = remoteListIdFor(local.getListId());
            if (remoteListId == null) {
                continue;
            }
            RemoteTask created = client.createTask(remoteListId, local.getTitle());
            if (local.isCompleted()) {
                created = client.updateTask(remoteListId, created.id(), local.getTitle(), true);
            }
            local.setRemoteId(created.id());
            local.setUpdatedAt(created.updatedAt());
            counts.createdRemote++;
        }

        for (RemoteTask remote : remoteById.values()) {
            if (localByRemoteId.containsKey(remote.id()) || knownTaskIds.contains(remote.id())) {
                continue;
            }
            Long localListId = remoteToLocalList.get(remote.listId());
            if (localListId == null) {
                continue;
            }
            Task task = new Task(remote.title());
            task.setListId(localListId);
            task.setRemoteId(remote.id());
            task.setUpdatedAt(remote.updatedAt());
            if (remote.completed()) {
                task.markCompleted();
            }
            repository.save(task);
            counts.createdLocal++;
        }

        repository.persist();

        Set<String> linkedTaskIds = new HashSet<>();
        for (Task task : repository.findAll()) {
            if (task.getRemoteId() != null) {
                linkedTaskIds.add(task.getRemoteId());
            }
        }
        state.setTaskIds(linkedTaskIds);
        state.setLastSync(System.currentTimeMillis());
        stateStore.save(state);

        return counts.toResult();
    }

    private void syncLists(Counts counts) {
        List<TaskList> localLists = new ArrayList<>(repository.findAllLists());
        List<RemoteTaskList> remoteLists = client.listTaskLists();

        for (TaskList local : localLists) {
            if (local.getRemoteId() == null) {
                RemoteTaskList created = client.createTaskList(local.getName());
                local.setRemoteId(created.id());
                local.setUpdatedAt(created.updatedAt());
                counts.createdRemote++;
            }
        }

        for (RemoteTaskList remote : remoteLists) {
            boolean matched = localLists.stream()
                    .anyMatch(list -> remote.id().equals(list.getRemoteId()));
            if (matched) {
                continue;
            }
            TaskList created = repository.saveList(new TaskList(remote.title()));
            created.setRemoteId(remote.id());
            created.setUpdatedAt(remote.updatedAt());
            counts.createdLocal++;
        }
    }

    private void reconcileTask(Task local, RemoteTask remote, Counts counts) {
        String remoteListId = remoteListIdFor(local.getListId());
        boolean needsMove = remoteListId != null && !remoteListId.equals(remote.listId());

        if (needsMove) {
            client.deleteTask(remote.listId(), remote.id());
            RemoteTask created = client.createTask(remoteListId, local.getTitle());
            if (local.isCompleted()) {
                created = client.updateTask(remoteListId, created.id(), local.getTitle(), true);
            }
            local.setRemoteId(created.id());
            local.setUpdatedAt(created.updatedAt());
            counts.deletedRemote++;
            counts.createdRemote++;
            return;
        }

        boolean titleChanged = !remote.title().equals(local.getTitle());
        boolean statusChanged = remote.completed() != local.isCompleted();
        if (!titleChanged && !statusChanged) {
            return;
        }

        if (local.getUpdatedAt() >= remote.updatedAt()) {
            RemoteTask updated = client.updateTask(remote.listId(), remote.id(), local.getTitle(), local.isCompleted());
            local.setUpdatedAt(updated.updatedAt());
            counts.updatedRemote++;
        } else {
            local.rename(remote.title());
            if (remote.completed()) {
                local.markCompleted();
            } else {
                local.markPending();
            }
            local.setUpdatedAt(remote.updatedAt());
            counts.updatedLocal++;
        }
    }

    private List<RemoteTask> loadRemoteTasks() {
        List<RemoteTask> tasks = new ArrayList<>();
        for (RemoteTaskList list : client.listTaskLists()) {
            tasks.addAll(client.listTasks(list.id()));
        }
        return tasks;
    }

    private Map<String, Long> remoteToLocalListMap() {
        Map<String, Long> map = new HashMap<>();
        for (TaskList list : repository.findAllLists()) {
            if (list.getRemoteId() != null) {
                map.put(list.getRemoteId(), list.getId());
            }
        }
        return map;
    }

    private String remoteListIdFor(long localListId) {
        for (TaskList list : repository.findAllLists()) {
            if (list.getId() == localListId) {
                return list.getRemoteId();
            }
        }
        return null;
    }

    private static final class Counts {
        int createdLocal;
        int updatedLocal;
        int deletedLocal;
        int createdRemote;
        int updatedRemote;
        int deletedRemote;

        SyncResult toResult() {
            return new SyncResult(createdLocal, updatedLocal, deletedLocal,
                    createdRemote, updatedRemote, deletedRemote);
        }
    }
}
