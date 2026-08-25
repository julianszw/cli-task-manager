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
    public void execute(String[] args, TaskTrackerView view) {
        List<Task> removed = taskService.purgeCompletedTasks();
        if (removed.isEmpty()) {
            view.showMessage("No hay tareas completadas para eliminar");
            return;
        }

        List<String> lines = removed.stream()
                .map(task -> String.format("  - [#%d] %s", task.getId(), task.getTitle()))
                .toList();
        view.showMessage("Tareas completadas eliminadas:\n" + String.join("\n", lines));
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
