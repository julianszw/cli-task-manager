package tasktracker.cli;

import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class AddTaskCommand implements Command {

    private final TaskService taskService;

    public AddTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args, TaskTrackerView view) {
        if (args.length == 0) {
            view.showMessage("Uso: add <título de la tarea>");
            return;
        }

        String title = String.join(" ", args);
        Task task = taskService.addTask(title);
        view.showMessage(String.format("Tarea creada [#%d]: %s", task.getId(), task.getTitle()));
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Crea una nueva tarea. Uso: add <título>";
    }
}
