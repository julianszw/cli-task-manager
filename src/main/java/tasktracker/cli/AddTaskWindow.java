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
import tasktracker.provider.ProviderException;
import tasktracker.service.TaskService;

public class AddTaskWindow extends BasicWindow {

    private static final String TITLE = "Nueva tarea";
    private static final String EMPTY_TITLE = "⚠ El título no puede estar vacío";

    private final TaskService service;
    private final String listId;
    private final TextBox input = new TextBox(new TerminalSize(60, 1));
    private final TextBox dueInput = new TextBox(new TerminalSize(60, 1));
    private final Label message = new Label("");
    private Task created;

    public AddTaskWindow(TaskService service, String listId) {
        super(TITLE);
        this.service = service;
        this.listId = listId;

        setHints(List.of(Window.Hint.CENTERED));

        message.setForegroundColor(VisualStyle.ERROR);
        dueInput.setText(Dates.today());

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(new Label("Título de la tarea:"));
        content.addComponent(input.withBorder(new RoundedBorder()));
        content.addComponent(new Label("Fecha (yyyy-MM-dd, opcional):"));
        content.addComponent(dueInput.withBorder(new RoundedBorder()));
        content.addComponent(new Label("Enter para confirmar · Tab para ir a fecha · Esc para cancelar"));
        content.addComponent(message);
        setComponent(content);

        setFocusedInteractable(input);
    }

    public Task getCreatedTask() {
        return created;
    }

    String getMessageText() {
        return message.getText();
    }

    TextBox inputBox() {
        return input;
    }

    TextBox dueBox() {
        return dueInput;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Tab) {
            setFocusedInteractable(dueInput);
            return true;
        }
        if (key.getKeyType() == KeyType.ReverseTab) {
            setFocusedInteractable(input);
            return true;
        }
        if (key.getKeyType() == KeyType.Enter) {
            String title = input.getText().trim();
            if (title.isEmpty()) {
                message.setText(EMPTY_TITLE);
                return true;
            }
            try {
                created = service.addTask(listId, title, dueInput.getText());
                close();
            } catch (IllegalArgumentException | ProviderException e) {
                message.setText(e.getMessage());
            }
            return true;
        }
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return super.handleInput(key);
    }
}
