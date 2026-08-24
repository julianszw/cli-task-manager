package tasktracker.cli;

import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class PurgeCompletedCommand implements Command {

    private final TaskService taskService;

    public PurgeCompletedCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args) {
        List<Task> removed = taskService.purgeCompletedTasks();
        if (removed.isEmpty()) {
            System.out.println("No hay tareas completadas para eliminar");
            return;
        }
        System.out.println("Tareas completadas eliminadas:");
        for (Task task : removed) {
            System.out.printf("#%-4d %s%n", task.getId(), task.getTitle());
        }
    }

    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "Elimina todas las tareas completadas. Uso: purge";
    }
}
