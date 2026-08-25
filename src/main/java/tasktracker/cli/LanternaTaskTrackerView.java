package tasktracker.cli;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import tasktracker.service.TaskService;

public class LanternaTaskTrackerView implements TaskTrackerView {

    private final WindowBasedTextGUI gui;
    private final TaskService service;
    private MainWindow mainWindow;

    public LanternaTaskTrackerView(WindowBasedTextGUI gui, TaskService service) {
        this.gui = gui;
        this.service = service;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void showMessage(String message) {
        mainWindow.setOutput(message);
    }

    @Override
    public void showTaskList() {
        gui.addWindowAndWait(new TaskListWindow(service));
        gui.setActiveWindow(mainWindow);
        mainWindow.focusInput();
    }

    @Override
    public void exit() {
        mainWindow.close();
    }
}
