package tasktracker.model;

public class TaskList {

    private long id;
    private final String name;

    public TaskList(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }
}
