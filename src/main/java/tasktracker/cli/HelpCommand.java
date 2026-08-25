package tasktracker.cli;

import java.util.List;

public class HelpCommand implements Command {

    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(String[] args, TaskTrackerView view) {
        List<Command> commands = registry.getAll();
        StringBuilder help = new StringBuilder("Comandos disponibles:");
        for (Command command : commands) {
            help.append('\n')
                    .append(String.format("%-10s %s", command.getName(), command.getDescription()));
        }
        view.showMessage(help.toString());
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
