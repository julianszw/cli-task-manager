package tasktracker.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class InteractiveTaskBrowser {

    private static final String NO_TASKS = "No hay tareas cargadas";
    private static final String NO_COMPLETED_TO_PURGE = "No hay tareas completadas para eliminar";
    private static final String HELP =
            "Teclas: ↑/k subir · ↓/j bajar · c completar · d eliminar · p purgar · b atrás · q/Esc salir";

    private final TaskService taskService;
    private final PrintStream out;
    private final TaskTableFormatter formatter = new TaskTableFormatter();

    public InteractiveTaskBrowser(TaskService taskService, PrintStream out) {
        this.taskService = taskService;
        this.out = out;
    }

    public void run(KeySource source) {
        List<Task> tasks = taskService.listTasks();
        if (tasks.isEmpty()) {
            out.println(NO_TASKS);
            out.println(HELP);
            waitForExit(source);
            return;
        }

        int selected = 0;
        String message = null;
        boolean running = true;
        while (running) {
            render(tasks, selected, message);
            message = null;

            switch (readKey(source)) {
                case UP -> selected = Math.max(0, selected - 1);
                case DOWN -> selected = Math.min(tasks.size() - 1, selected + 1);
                case COMPLETE -> {
                    taskService.completeTask(tasks.get(selected).getId());
                    tasks = taskService.listTasks();
                }
                case DELETE -> {
                    taskService.deleteTask(tasks.get(selected).getId());
                    tasks = taskService.listTasks();
                    if (tasks.isEmpty()) {
                        out.println(NO_TASKS);
                        return;
                    }
                    selected = Math.min(selected, tasks.size() - 1);
                }
                case PURGE -> {
                    List<Task> removed = taskService.purgeCompletedTasks();
                    tasks = taskService.listTasks();
                    if (removed.isEmpty()) {
                        message = NO_COMPLETED_TO_PURGE;
                    }
                    if (tasks.isEmpty()) {
                        out.println(NO_TASKS);
                        return;
                    }
                    selected = Math.min(selected, tasks.size() - 1);
                }
                case EXIT, BACK -> running = false;
            }
        }
    }

    private Key readKey(KeySource source) {
        try {
            return source.readKey();
        } catch (IOException e) {
            return Key.EXIT;
        }
    }

    private void waitForExit(KeySource source) {
        while (readKey(source) != Key.EXIT) {
            // esperar hasta que el usuario salga del modo
        }
    }

    private void render(List<Task> tasks, int selected, String message) {
        clearScreen();
        if (message != null) {
            out.println(message);
        }
        out.println(formatter.render(tasks, selected));
        out.println(HELP);
    }

    private void clearScreen() {
        out.print("\033[2J\033[H");
    }
}
