package tasktracker.cli;

import java.io.IOException;
import tasktracker.service.TaskService;

public class ListTasksCommand implements Command {

    private final TaskService taskService;

    public ListTasksCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void execute(String[] args) {
        try (KeyReader keyReader = new KeyReader()) {
            new InteractiveTaskBrowser(taskService, System.out).run(keyReader);
        } catch (IOException e) {
            System.out.println("No se pudo iniciar el modo interactivo: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Navega las tareas de forma interactiva. Uso: list";
    }
}
