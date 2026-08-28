package tasktracker.sync;

public record RemoteTask(String id, String title, boolean completed, String listId, long updatedAt) {
}
