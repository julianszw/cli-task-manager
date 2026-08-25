package tasktracker.cli;

public class ExitCommand implements Command {

    @Override
    public void execute(String[] args, TaskTrackerView view) {
        view.exit();
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Sale de la aplicación. Uso: exit";
    }
}
