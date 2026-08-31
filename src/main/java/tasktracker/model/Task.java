package tasktracker.model;

import java.time.LocalDate;

public class Task {

    private String id;
    private String title;
    private TaskStatus status;
    private String listId;
    private LocalDate due;

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

    public LocalDate getDue() {
        return due;
    }

    public void setDue(LocalDate due) {
        this.due = due;
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
