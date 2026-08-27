package tasktracker.cli;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

public class EditTaskWindow extends BasicWindow {

    private static final String TITLE = "Editar tarea";
    private static final String EMPTY_TITLE = "⚠ El título no puede estar vacío";

    private final TaskService service;
    private final Task task;
    private final TextBox input = new TextBox(new TerminalSize(60, 1));
    private final Label message = new Label("");
    private boolean updated;

    public EditTaskWindow(TaskService service, Task task) {
        super(TITLE);
        this.service = service;
        this.task = task;

        setHints(List.of(Window.Hint.CENTERED));

        message.setForegroundColor(VisualStyle.ERROR);

        input.setText(task.getTitle());

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(new Label("Título de la tarea:"));
        content.addComponent(input.withBorder(new RoundedBorder()));
        content.addComponent(new Label("Enter para confirmar · Esc para cancelar"));
        content.addComponent(message);
        setComponent(content);

        setFocusedInteractable(input);
    }

    boolean isUpdated() {
        return updated;
    }

    String getMessageText() {
        return message.getText();
    }

    TextBox inputBox() {
        return input;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter) {
            String title = input.getText().trim();
            if (title.isEmpty()) {
                message.setText(EMPTY_TITLE);
                return true;
            }
            service.renameTask(task.getId(), title);
            updated = true;
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return super.handleInput(key);
    }
}
