package tasktracker.model;

public class TaskList {

    private String id;
    private final String title;

    public TaskList(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }
}
