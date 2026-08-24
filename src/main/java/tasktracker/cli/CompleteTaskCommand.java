package tasktracker.cli;

import tasktracker.service.TaskService;

public class CompleteTaskCommand implements Command {

    private final TaskService taskService;

    public CompleteTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: complete <id>");
            return;
        }

        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("El id debe ser un número");
            return;
        }

        taskService.completeTask(id);
        System.out.printf("Tarea #%d completada%n", id);
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
