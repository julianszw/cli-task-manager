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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import tasktracker.cli.LanternaTaskTrackerView;
import tasktracker.cli.MainMenuWindow;
import tasktracker.repository.JsonTaskRepository;
import tasktracker.service.TaskService;

public class App {

    private static final String DATA_FILE = "tasks.json";

    public static void main(String[] args) throws IOException {
        List<String> startupWarnings = new ArrayList<>();
        AtomicReference<Consumer<String>> warningSink = new AtomicReference<>(startupWarnings::add);

        JsonTaskRepository repository = new JsonTaskRepository(
                Path.of(DATA_FILE),
                message -> warningSink.get().accept(message));
        TaskService service = new TaskService(repository);

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        try {
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);

            LanternaTaskTrackerView view = new LanternaTaskTrackerView(gui, service);

            MainMenuWindow mainMenuWindow = new MainMenuWindow(view, service);
            view.setMainWindow(mainMenuWindow);

            warningSink.set(view::showMessage);
            startupWarnings.forEach(view::showMessage);

            gui.addWindowAndWait(mainMenuWindow);
        } finally {
            screen.stopScreen();
        }
    }
}
