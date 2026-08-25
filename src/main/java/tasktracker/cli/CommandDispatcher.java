package tasktracker.cli;

import java.util.Arrays;
import tasktracker.exception.TaskNotFoundException;

public class CommandDispatcher {

    private final CommandRegistry registry;

    public CommandDispatcher(CommandRegistry registry) {
        this.registry = registry;
    }

    public void dispatch(String line, TaskTrackerView view) {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        String[] tokens = trimmed.split("\\s+");
        String name = tokens[0];
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        Command command = registry.find(name);
        if (command == null) {
            view.showMessage("Comando no reconocido: " + name);
            return;
        }

        try {
            command.execute(args, view);
        } catch (IllegalArgumentException | TaskNotFoundException e) {
            view.showMessage(e.getMessage());
        }
    }
}
