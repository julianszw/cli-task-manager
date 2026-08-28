package tasktracker.cli;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import tasktracker.service.TaskService;

public class LanternaTaskTrackerView {

    private final WindowBasedTextGUI gui;
    private final TaskService service;

    public LanternaTaskTrackerView(WindowBasedTextGUI gui, TaskService service) {
        this.gui = gui;
        this.service = service;
    }

    public void start() {
        TaskListWindow window = new TaskListWindow(service, gui);
        gui.addWindowAndWait(window);
    }
}
