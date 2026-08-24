package tasktracker.cli;

import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class ListTasksCommand implements Command {

    private final TaskService taskService;

    public ListTasksCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args) {
        List<Task> tasks = taskService.listTasks();
        if (tasks.isEmpty()) {
            System.out.println("No hay tareas cargadas");
            return;
        }
        for (Task task : tasks) {
            System.out.printf("#%-4d [%s] %s%n", task.getId(), task.getStatus(), task.getTitle());
        }
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lista todas las tareas. Uso: list";
    }
}
