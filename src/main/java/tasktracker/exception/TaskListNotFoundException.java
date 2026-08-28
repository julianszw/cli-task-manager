package tasktracker.exception;

public class TaskListNotFoundException extends RuntimeException {

    public TaskListNotFoundException(String id) {
        super("No existe una lista con id " + id);
    }
}
