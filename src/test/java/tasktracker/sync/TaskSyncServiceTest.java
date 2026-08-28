package tasktracker.sync;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.repository.TaskRepository;
import tasktracker.service.TaskService;

class TaskSyncServiceTest {

    @TempDir
    Path tempDir;

    private final TaskRepository repository = new InMemoryTaskRepository();
    private final TaskService service = new TaskService(repository);
    private final FakeGoogleTasksClient client = new FakeGoogleTasksClient();

    private TaskSyncService syncService() {
        return new TaskSyncService(repository, client, new SyncStateStore(tempDir.resolve("sync-state.json")));
    }

    private long inbox() {
        return service.createList("Inbox").getId();
    }

    @Test
    void pushesLocalListsAndTasksToRemote() {
        long inboxId = inbox();
        service.addTask(inboxId, "Comprar leche");

        SyncResult result = syncService().sync();

        assertEquals(1, client.lists.size());
        assertEquals("Inbox", client.lists.values().iterator().next().title);
        assertEquals(1, client.tasks.size());
        assertEquals("Comprar leche", client.tasks.values().iterator().next().title);
        assertEquals(2, result.createdRemote());
        assertNotNull(service.listLists().get(0).getRemoteId());
        assertNotNull(service.listTasks(inboxId).get(0).getRemoteId());
    }

    @Test
    void pullsRemoteListsAndTasksToLocal() {
        client.seedList("rl1", "Trabajo", 1000);
        client.seedTask("t1", "rl1", "Pagar facturas", true, 1000);

        SyncResult result = syncService().sync();

        assertEquals(1, service.listLists().size());
        assertEquals("Trabajo", service.listLists().get(0).getName());
        List<Task> tasks = service.listTasks(service.listLists().get(0).getId());
        assertEquals(1, tasks.size());
        assertEquals("Pagar facturas", tasks.get(0).getTitle());
        assertEquals(TaskStatus.COMPLETED, tasks.get(0).getStatus());
        assertEquals("t1", tasks.get(0).getRemoteId());
        assertEquals(2, result.createdLocal());
    }

    @Test
    void pushesLocalCompletionToRemote() {
        long inboxId = inbox();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        service.completeTask(task.getId());
        SyncResult result = syncService().sync();

        assertEquals(1, result.updatedRemote());
        assertTrue(client.tasks.values().iterator().next().completed);
    }

    @Test
    void pullsRemoteCompletionToLocal() {
        long inboxId = inbox();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        FakeGoogleTasksClient.FakeTask remote = client.tasks.values().iterator().next();
        remote.completed = true;
        remote.updatedAt = 5000L;

        SyncResult result = syncService().sync();

        assertEquals(1, result.updatedLocal());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void conflictResolvedByLastModificationWinsLocal() {
        long inboxId = inbox();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        task.rename("Local");
        task.setUpdatedAt(6000L);
        FakeGoogleTasksClient.FakeTask remote = client.tasks.values().iterator().next();
        remote.title = "Remoto";
        remote.updatedAt = 5000L;

        SyncResult result = syncService().sync();

        assertEquals("Local", client.tasks.values().iterator().next().title);
        assertEquals(1, result.updatedRemote());
    }

    @Test
    void conflictResolvedByLastModificationWinsRemote() {
        long inboxId = inbox();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        task.rename("Local");
        task.setUpdatedAt(5000L);
        FakeGoogleTasksClient.FakeTask remote = client.tasks.values().iterator().next();
        remote.title = "Remoto";
        remote.updatedAt = 6000L;

        SyncResult result = syncService().sync();

        assertEquals("Remoto", task.getTitle());
        assertEquals(1, result.updatedLocal());
    }

    @Test
    void pushesLocalDeletionToRemote() {
        long inboxId = inbox();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        service.deleteTask(task.getId());
        SyncResult result = syncService().sync();

        assertEquals(0, client.tasks.size());
        assertEquals(1, result.deletedRemote());
    }

    @Test
    void pullsRemoteDeletionToLocal() {
        long inboxId = inbox();
        service.addTask(inboxId, "A");
        syncService().sync();

        client.tasks.clear();
        SyncResult result = syncService().sync();

        assertTrue(service.listTasks(inboxId).isEmpty());
        assertEquals(1, result.deletedLocal());
    }

    @Test
    void localDeletionIsNotResurrectedOnNextSync() {
        long inboxId = inbox();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        service.deleteTask(task.getId());
        syncService().sync();
        SyncResult second = syncService().sync();

        assertEquals(0, client.tasks.size());
        assertTrue(service.listTasks(inboxId).isEmpty());
        assertTrue(second.isEmpty());
    }

    @Test
    void movesTaskToAnotherListRemotely() {
        long inboxId = inbox();
        long workId = service.createList("Trabajo").getId();
        Task task = service.addTask(inboxId, "A");
        syncService().sync();

        service.moveTask(task.getId(), workId);
        SyncResult result = syncService().sync();

        String remoteWorkId = service.listLists().stream()
                .filter(list -> list.getId() == workId)
                .findFirst().orElseThrow().getRemoteId();
        assertEquals(1, client.tasks.size());
        assertEquals(remoteWorkId, client.tasks.values().iterator().next().listId);
        assertEquals(1, result.deletedRemote());
        assertEquals(1, result.createdRemote());
    }

    @Test
    void ensureAuthenticatedAuthorizesWhenNeeded() {
        client.authenticated = false;

        syncService().ensureAuthenticated();

        assertTrue(client.authenticated);
    }

    @Test
    void isAuthenticatedDelegatesToClient() {
        TaskSyncService service = syncService();

        client.authenticated = false;
        assertEquals(false, service.isAuthenticated());

        client.authenticated = true;
        assertEquals(true, service.isAuthenticated());
    }

    static final class FakeGoogleTasksClient implements GoogleTasksClient {

        final LinkedHashMap<String, FakeList> lists = new LinkedHashMap<>();
        final LinkedHashMap<String, FakeTask> tasks = new LinkedHashMap<>();
        boolean authenticated = true;
        private long now = 1_000L;
        private int listSeq = 0;
        private int taskSeq = 0;

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void authorize() {
            authenticated = true;
        }

        @Override
        public List<RemoteTaskList> listTaskLists() {
            List<RemoteTaskList> result = new ArrayList<>();
            for (FakeList list : lists.values()) {
                result.add(new RemoteTaskList(list.id, list.title, list.updatedAt));
            }
            return result;
        }

        @Override
        public List<RemoteTask> listTasks(String remoteListId) {
            List<RemoteTask> result = new ArrayList<>();
            for (FakeTask task : tasks.values()) {
                if (task.listId.equals(remoteListId)) {
                    result.add(new RemoteTask(task.id, task.title, task.completed, task.listId, task.updatedAt));
                }
            }
            return result;
        }

        @Override
        public RemoteTaskList createTaskList(String title) {
            now += 1;
            String id = "rl" + (++listSeq);
            FakeList list = new FakeList(id, title, now);
            lists.put(id, list);
            return new RemoteTaskList(list.id, list.title, list.updatedAt);
        }

        @Override
        public RemoteTask createTask(String remoteListId, String title) {
            now += 1;
            String id = "t" + (++taskSeq);
            FakeTask task = new FakeTask(id, title, false, remoteListId, now);
            tasks.put(id, task);
            return new RemoteTask(task.id, task.title, task.completed, task.listId, task.updatedAt);
        }

        @Override
        public RemoteTask updateTask(String remoteListId, String taskId, String title, boolean completed) {
            now += 1;
            FakeTask task = tasks.get(taskId);
            task.title = title;
            task.completed = completed;
            task.updatedAt = now;
            return new RemoteTask(task.id, task.title, task.completed, task.listId, task.updatedAt);
        }

        @Override
        public void deleteTask(String remoteListId, String taskId) {
            tasks.remove(taskId);
        }

        void seedList(String id, String title, long updatedAt) {
            lists.put(id, new FakeList(id, title, updatedAt));
        }

        void seedTask(String id, String listId, String title, boolean completed, long updatedAt) {
            tasks.put(id, new FakeTask(id, title, completed, listId, updatedAt));
        }

        static final class FakeList {
            final String id;
            final String title;
            final long updatedAt;

            FakeList(String id, String title, long updatedAt) {
                this.id = id;
                this.title = title;
                this.updatedAt = updatedAt;
            }
        }

        static final class FakeTask {
            final String id;
            String title;
            boolean completed;
            final String listId;
            long updatedAt;

            FakeTask(String id, String title, boolean completed, String listId, long updatedAt) {
                this.id = id;
                this.title = title;
                this.completed = completed;
                this.listId = listId;
                this.updatedAt = updatedAt;
            }
        }
    }
}
