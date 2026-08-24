package tasktracker.model;

public class Task {

    private long id;
    private final String title;
    private TaskStatus status;

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

    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }
}
