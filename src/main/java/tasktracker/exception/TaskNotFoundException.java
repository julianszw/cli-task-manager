package tasktracker.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(long id) {
        super("No existe una tarea con id " + id);
    }
}
