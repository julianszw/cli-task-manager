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
import tasktracker.google.GoogleAuth;
import tasktracker.google.HttpGoogleTasksClient;
import tasktracker.repository.JsonTaskRepository;
import tasktracker.service.TaskService;
import tasktracker.sync.SyncException;
import tasktracker.sync.SyncStateStore;
import tasktracker.sync.TaskSyncService;

public class App {

    private static final String DATA_FILE = "tasks.json";
    private static final String SYNC_STATE_FILE = "sync-state.json";

    public static void main(String[] args) throws IOException {
        List<String> startupWarnings = new ArrayList<>();

        JsonTaskRepository repository = new JsonTaskRepository(
                Path.of(DATA_FILE),
                startupWarnings::add);
        TaskService service = new TaskService(repository);
        if (service.listLists().isEmpty()) {
            service.createList("Inbox");
        }

        GoogleAuth googleAuth = new GoogleAuth(Path.of("."));
        HttpGoogleTasksClient googleClient = new HttpGoogleTasksClient(googleAuth);
        TaskSyncService syncService = new TaskSyncService(
                repository,
                googleClient,
                new SyncStateStore(Path.of(SYNC_STATE_FILE)));

        if (googleAuth.hasStoredCredentials()) {
            try {
                syncService.sync();
            } catch (SyncException e) {
                startupWarnings.add(e.getMessage());
            }
        }

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        try {
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            gui.setTheme(VisualStyle.theme());

            LanternaTaskTrackerView view = new LanternaTaskTrackerView(gui, screen, service, syncService);
            view.start(startupWarnings);
        } finally {
            screen.stopScreen();
        }
    }
}
