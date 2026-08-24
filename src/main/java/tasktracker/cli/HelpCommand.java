package tasktracker.cli;

import java.util.List;

public class HelpCommand implements Command {

    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(String[] args) {
        List<Command> commands = registry.getAll();
        for (Command command : commands) {
            System.out.printf("%-10s %s%n", command.getName(), command.getDescription());
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Muestra todos los comandos disponibles. Uso: help";
    }
}
