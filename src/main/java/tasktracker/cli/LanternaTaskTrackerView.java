package tasktracker.cli;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import java.util.List;
import tasktracker.service.TaskService;
import tasktracker.sync.TaskSyncService;

public class LanternaTaskTrackerView {

    private final WindowBasedTextGUI gui;
    private final Screen screen;
    private final TaskService service;
    private final TaskSyncService syncService;

    public LanternaTaskTrackerView(WindowBasedTextGUI gui, Screen screen,
                                   TaskService service, TaskSyncService syncService) {
        this.gui = gui;
        this.screen = screen;
        this.service = service;
        this.syncService = syncService;
    }

    public void start(List<String> startupWarnings) {
        TaskListWindow window = new TaskListWindow(service, gui, syncService, screen);
        if (!startupWarnings.isEmpty()) {
            window.setWarning(String.join(" · ", startupWarnings));
        }
        gui.addWindowAndWait(window);
    }
}
