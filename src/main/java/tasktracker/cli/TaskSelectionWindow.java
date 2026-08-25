package tasktracker.cli;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.ArrayList;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class TaskSelectionWindow extends BasicWindow {

    public enum Mode {
        COMPLETE,
        REOPEN
    }

    private static final String HELP = "↑/↓ seleccionar · Enter confirmar · Esc cancelar";
    private static final String NO_TASKS = "No hay tareas para seleccionar";

    private final TaskService service;
    private final Mode mode;
    private final List<Task> tasks = new ArrayList<>();
    private final Table<String> table = new Table<>("ID", "ESTADO", "TÍTULO");
    private final Label status = new Label("");

    public TaskSelectionWindow(TaskService service, Mode mode) {
        super(mode == Mode.COMPLETE ? "Completar tarea" : "Reabrir tarea");
        this.service = service;
        this.mode = mode;

        setHints(List.of(Window.Hint.CENTERED));

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(table.withBorder(Borders.singleLine("Tareas")));
        content.addComponent(status);
        content.addComponent(new Label(HELP));
        setComponent(content);

        table.setTableCellRenderer(new TaskCellRenderer(tasks));
        refresh();
        setFocusedInteractable(table);
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
        } else {
            status.setText("");
        }
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter) {
            applySelected();
            return true;
        }
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null && (c == 'b' || c == 'q')) {
                close();
                return true;
            }
        }
        return super.handleInput(key);
    }

    private void applySelected() {
        int selected = table.getSelectedRow();
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        Task task = tasks.get(selected);
        if (mode == Mode.COMPLETE) {
            service.completeTask(task.getId());
        } else {
            service.reopenTask(task.getId());
        }
        close();
    }
}
