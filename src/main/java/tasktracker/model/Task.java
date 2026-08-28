package tasktracker.model;

public class Task {

    private String id;
    private String title;
    private TaskStatus status;
    private String listId;
    private String due;

    public Task(String title) {
        this.title = title;
        this.status = TaskStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getDueDate() {
        if (due == null) {
            return null;
        }
        int separator = due.indexOf('T');
        return separator >= 0 ? due.substring(0, separator) : due;
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
