package tasktracker.exception;

public class TaskNotFoundException extends RuntimeException {

    private final long id;

    public TaskNotFoundException(long id) {
        super("No existe una tarea con id " + id);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
