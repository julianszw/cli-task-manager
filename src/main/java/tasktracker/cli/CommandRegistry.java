package tasktracker.cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    public Command find(String name) {
        return commands.get(name);
    }

    public boolean contains(String name) {
        return commands.containsKey(name);
    }

    public List<Command> getAll() {
        return new ArrayList<>(commands.values());
    }
}
