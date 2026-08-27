package tasktracker;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import tasktracker.cli.LanternaTaskTrackerView;
import tasktracker.cli.VisualStyle;
import tasktracker.repository.JsonTaskRepository;
import tasktracker.service.TaskService;

public class App {

    private static final String DATA_FILE = "tasks.json";

    public static void main(String[] args) throws IOException {
        List<String> startupWarnings = new ArrayList<>();

        JsonTaskRepository repository = new JsonTaskRepository(
                Path.of(DATA_FILE),
                startupWarnings::add);
        TaskService service = new TaskService(repository);
        if (service.listLists().isEmpty()) {
            service.createList("Inbox");
        }

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        try {
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            gui.setTheme(VisualStyle.theme());

            LanternaTaskTrackerView view = new LanternaTaskTrackerView(gui, service);
            view.start(startupWarnings);
        } finally {
            screen.stopScreen();
        }
    }
}
