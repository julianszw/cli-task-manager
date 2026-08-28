package tasktracker.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("No existe una tarea con id " + id);
    }
}
