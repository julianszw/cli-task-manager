package tasktracker.cli;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class TaskListWindow extends BasicWindow {

    private static final String TITLE = "Tareas";
    private static final String HELP =
            "Teclas: ↑/k subir · ↓/j bajar · a crear · c completar · r reabrir · d eliminar · p purgar · t tema · q/Esc salir";
    private static final String NO_TASKS = "No hay tareas cargadas";
    private static final String NO_COMPLETED_TO_PURGE = "No hay tareas completadas para eliminar";

    private final TaskService service;
    private final WindowBasedTextGUI gui;
    private final ThemeManager themeManager;
    private final List<Task> tasks = new ArrayList<>();
    private final Table<String> table = new Table<>("ID", "ESTADO", "TÍTULO");
    private final Label status = new Label("");

    public TaskListWindow(TaskService service) {
        this(service, null, new ThemeManager());
    }

    public TaskListWindow(TaskService service, WindowBasedTextGUI gui) {
        this(service, gui, new ThemeManager());
    }

    public TaskListWindow(TaskService service, WindowBasedTextGUI gui, ThemeManager themeManager) {
        super(TITLE);
        this.service = service;
        this.gui = gui;
        this.themeManager = themeManager;

        setHints(List.of(Window.Hint.FULL_SCREEN));

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(table.withBorder(Borders.singleLine("Tareas")));
        content.addComponent(status);
        content.addComponent(new Label(HELP));
        setComponent(content);

        table.setTableCellRenderer(new TaskCellRenderer(tasks, themeManager::isDark));
        refresh();
        setFocusedInteractable(table);
    }

    public boolean isDarkTheme() {
        return themeManager.isDark();
    }

    void setStatus(String message) {
        status.setText(message);
    }

    String getStatusText() {
        return status.getText();
    }

    private void refresh() {
        tasks.clear();
        tasks.addAll(service.listTasks());

        table.getTableModel().clear();
        for (Task task : tasks) {
            table.getTableModel().addRow(
                    Long.toString(task.getId()),
                    task.getStatus().name(),
                    task.getTitle());
        }

        if (tasks.isEmpty()) {
            status.setText(NO_TASKS);
            return;
        }
        if (table.getSelectedRow() >= tasks.size()) {
            table.setSelectedRow(tasks.size() - 1);
        }
        status.setText("");
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null) {
                switch (c) {
                    case 'k' -> {
                        moveUp();
                        return true;
                    }
                    case 'j' -> {
                        moveDown();
                        return true;
                    }
                    case 'a' -> {
                        openAddTask();
                        return true;
                    }
                    case 'c' -> {
                        completeSelected();
                        return true;
                    }
                    case 'r' -> {
                        reopenSelected();
                        return true;
                    }
                    case 'd' -> {
                        deleteSelected();
                        return true;
                    }
                    case 'p' -> {
                        purgeCompleted();
                        return true;
                    }
                    case 't' -> {
                        toggleTheme();
                        return true;
                    }
                    case 'q' -> {
                        close();
                        return true;
                    }
                    default -> {
                    }
                }
            }
        } else if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return super.handleInput(key);
    }

    private void moveUp() {
        if (!tasks.isEmpty()) {
            table.setSelectedRow(Math.max(0, table.getSelectedRow() - 1));
        }
    }

    private void moveDown() {
        if (!tasks.isEmpty()) {
            table.setSelectedRow(Math.min(tasks.size() - 1, table.getSelectedRow() + 1));
        }
    }

    private void openAddTask() {
        if (gui == null) {
            return;
        }
        gui.addWindowAndWait(new AddTaskWindow(service));
        refresh();
    }

    private void toggleTheme() {
        themeManager.toggle();
        if (gui != null) {
            gui.setTheme(themeManager.current());
            try {
                gui.updateScreen();
            } catch (IOException e) {
                // el repintado se reintentará en el siguiente evento de la GUI
            }
        }
    }

    private void completeSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        service.completeTask(tasks.get(selected).getId());
        refresh();
    }

    private void reopenSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        service.reopenTask(tasks.get(selected).getId());
        refresh();
    }

    private void deleteSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        service.deleteTask(tasks.get(selected).getId());
        refresh();
    }

    private void purgeCompleted() {
        List<Task> removed = service.purgeCompletedTasks();
        refresh();
        if (removed.isEmpty()) {
            status.setText(NO_COMPLETED_TO_PURGE);
        } else {
            status.setText("Tareas completadas eliminadas: " + removed.size());
        }
    }
}
