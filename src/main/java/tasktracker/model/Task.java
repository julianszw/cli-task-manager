package tasktracker.model;

public class Task {

    private long id;
    private String title;
    private TaskStatus status;
    private long listId;

    public Task(String title) {
        this.title = title;
        this.status = TaskStatus.PENDING;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    public void markPending() {
        this.status = TaskStatus.PENDING;
    }

    public void rename(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }
}
