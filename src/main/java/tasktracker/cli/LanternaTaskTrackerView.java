package tasktracker.cli;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class LanternaTaskTrackerView implements TaskTrackerView {

    private final WindowBasedTextGUI gui;
    private final TaskService service;
    private MainMenuWindow mainMenuWindow;

    public LanternaTaskTrackerView(WindowBasedTextGUI gui, TaskService service) {
        this.gui = gui;
        this.service = service;
    }

    public void setMainWindow(MainMenuWindow mainMenuWindow) {
        this.mainMenuWindow = mainMenuWindow;
    }

    @Override
    public void showMessage(String message) {
        mainMenuWindow.setOutput(message);
    }

    @Override
    public void showTaskList() {
        gui.addWindowAndWait(new TaskListWindow(service));
        backToMenu();
    }

    @Override
    public void showAddTask() {
        AddTaskWindow window = new AddTaskWindow(service);
        gui.addWindowAndWait(window);
        Task created = window.getCreatedTask();
        if (created != null) {
            showMessage(String.format("Tarea creada [#%d]: %s", created.getId(), created.getTitle()));
        }
        backToMenu();
    }

    @Override
    public void showCompleteTask() {
        showSelection(TaskSelectionWindow.Mode.COMPLETE);
    }

    @Override
    public void showReopenTask() {
        showSelection(TaskSelectionWindow.Mode.REOPEN);
    }

    @Override
    public void exit() {
        mainMenuWindow.close();
    }

    private void showSelection(TaskSelectionWindow.Mode mode) {
        gui.addWindowAndWait(new TaskSelectionWindow(service, mode));
        backToMenu();
    }

    private void backToMenu() {
        gui.setActiveWindow(mainMenuWindow);
    }
}
