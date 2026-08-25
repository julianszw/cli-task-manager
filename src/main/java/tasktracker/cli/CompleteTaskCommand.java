package tasktracker.cli;

import tasktracker.service.TaskService;

public class CompleteTaskCommand implements Command {

    private final TaskService taskService;

    public CompleteTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args, TaskTrackerView view) {
        if (args.length == 0) {
            view.showMessage("Uso: complete <id>");
            return;
        }

        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            view.showMessage("El id debe ser un número");
            return;
        }

        taskService.completeTask(id);
        view.showMessage(String.format("Tarea #%d completada", id));
    }

    @Override
    public String getName() {
        return "complete";
    }

    @Override
    public String getDescription() {
        return "Marca una tarea como completada. Uso: complete <id>";
    }
}
