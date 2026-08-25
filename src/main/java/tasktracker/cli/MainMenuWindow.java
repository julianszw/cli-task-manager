package tasktracker.cli;

import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class MainMenuWindow extends BasicWindow {

    private static final String TITLE = "CLI Task Tracker";
    private static final String HELP =
            "(a) Add task · (l) List tasks · (c) Complete task · (r) Reopen task · (p) Purge completed · (h) Help · (x) Exit";

    private final TaskTrackerView view;
    private final TaskService service;
    private final ActionListBox menu = new ActionListBox();
    private final Label output = new Label("");

    public MainMenuWindow(TaskTrackerView view, TaskService service) {
        super(TITLE);
        this.view = view;
        this.service = service;

        setHints(List.of(Window.Hint.FULL_SCREEN));

        menu.addItem("(a) Add task", () -> view.showAddTask());
        menu.addItem("(l) List tasks", () -> view.showTaskList());
        menu.addItem("(c) Complete task", () -> view.showCompleteTask());
        menu.addItem("(r) Reopen task", () -> view.showReopenTask());
        menu.addItem("(p) Purge completed", this::purgeCompleted);
        menu.addItem("(h) Help", this::showHelp);
        menu.addItem("(x) Exit", () -> view.exit());
        menu.setSelectedIndex(0);

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(new Label("Selecciona una opción:"));
        content.addComponent(menu.withBorder(Borders.singleLine("Menú")));
        content.addComponent(new Label("Resultado:"));
        content.addComponent(output);
        setComponent(content);

        setFocusedInteractable(menu);
    }

    public void setOutput(String message) {
        output.setText(message);
    }

    public int getSelectedIndex() {
        return menu.getSelectedIndex();
    }

    String getOutputText() {
        return output.getText();
    }

    private void purgeCompleted() {
        List<Task> removed = service.purgeCompletedTasks();
        output.setText(removed.isEmpty()
                ? "No hay tareas completadas para eliminar"
                : "Tareas completadas eliminadas: " + removed.size());
    }

    private void showHelp() {
        output.setText(HELP);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null) {
                switch (c) {
                    case 'a' -> {
                        activate(0);
                        return true;
                    }
                    case 'l' -> {
                        activate(1);
                        return true;
                    }
                    case 'c' -> {
                        activate(2);
                        return true;
                    }
                    case 'r' -> {
                        activate(3);
                        return true;
                    }
                    case 'p' -> {
                        activate(4);
                        return true;
                    }
                    case 'h' -> {
                        activate(5);
                        return true;
                    }
                    case 'x' -> {
                        activate(6);
                        return true;
                    }
                    case 'k' -> {
                        moveUp();
                        return true;
                    }
                    case 'j' -> {
                        moveDown();
                        return true;
                    }
                    default -> {
                    }
                }
            }
        }
        return super.handleInput(key);
    }

    private void activate(int index) {
        menu.setSelectedIndex(index);
        Runnable action = menu.getSelectedItem();
        if (action != null) {
            action.run();
        }
    }

    private void moveUp() {
        menu.setSelectedIndex(Math.max(0, menu.getSelectedIndex() - 1));
    }

    private void moveDown() {
        menu.setSelectedIndex(Math.min(menu.getItemCount() - 1, menu.getSelectedIndex() + 1));
    }
}
