package tasktracker.cli;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import java.util.List;
import tasktracker.service.TaskService;

public class LanternaTaskTrackerView {

    private final WindowBasedTextGUI gui;
    private final TaskService service;

    public LanternaTaskTrackerView(WindowBasedTextGUI gui, TaskService service) {
        this.gui = gui;
        this.service = service;
    }

    public void start(List<String> startupWarnings) {
        TaskListWindow window = new TaskListWindow(service, gui);
        if (!startupWarnings.isEmpty()) {
            window.setWarning(String.join(" · ", startupWarnings));
        }
        gui.addWindowAndWait(window);
    }
}
