package tasktracker.cli;

import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class AddTaskCommand implements Command {

    private final TaskService taskService;

    public AddTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: add <título de la tarea>");
            return;
        }

        String title = String.join(" ", args);
        Task task = taskService.addTask(title);
        System.out.printf("Tarea creada [#%d]: %s%n", task.getId(), task.getTitle());
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