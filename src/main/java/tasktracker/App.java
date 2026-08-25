package tasktracker;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import tasktracker.cli.AddTaskCommand;
import tasktracker.cli.CommandDispatcher;
import tasktracker.cli.CommandRegistry;
import tasktracker.cli.CompleteTaskCommand;
import tasktracker.cli.ExitCommand;
import tasktracker.cli.HelpCommand;
import tasktracker.cli.LanternaTaskTrackerView;
import tasktracker.cli.ListTasksCommand;
import tasktracker.cli.MainWindow;
import tasktracker.cli.PurgeCompletedCommand;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

public class App {

    public static void main(String[] args) throws IOException {
        TaskService service = new TaskService(new InMemoryTaskRepository());

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        try {
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);

            LanternaTaskTrackerView view = new LanternaTaskTrackerView(gui, service);

            CommandDispatcher dispatcher = new CommandDispatcher(buildRegistry(service));

            MainWindow mainWindow = new MainWindow(dispatcher, view);
            view.setMainWindow(mainWindow);

            gui.addWindowAndWait(mainWindow);
        } finally {
            screen.stopScreen();
        }
    }

    private static CommandRegistry buildRegistry(TaskService service) {
        CommandRegistry registry = new CommandRegistry();
        registry.register(new AddTaskCommand(service));
        registry.register(new ListTasksCommand());
        registry.register(new CompleteTaskCommand(service));
        registry.register(new PurgeCompletedCommand(service));
        registry.register(new HelpCommand(registry));
        registry.register(new ExitCommand());
        return registry;
    }
}
