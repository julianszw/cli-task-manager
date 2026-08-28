package tasktracker;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.nio.file.Path;
import tasktracker.cli.LanternaTaskTrackerView;
import tasktracker.cli.VisualStyle;
import tasktracker.google.GoogleAuth;
import tasktracker.google.GoogleTasksProvider;
import tasktracker.service.TaskService;

public class App {

    public static void main(String[] args) throws IOException {
        GoogleAuth auth = new GoogleAuth(Path.of("."));
        if (!auth.hasStoredCredentials()) {
            auth.authorize();
        }

        TaskService service = new TaskService(new GoogleTasksProvider(auth));
        service.load();
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
            view.start();
        } finally {
            screen.stopScreen();
        }
    }
}
