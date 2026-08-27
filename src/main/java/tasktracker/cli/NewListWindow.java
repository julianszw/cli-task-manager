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
import tasktracker.model.TaskList;
import tasktracker.service.TaskService;

public class NewListWindow extends BasicWindow {

    private static final String TITLE = "Nueva lista";
    private static final String EMPTY_NAME = "⚠ El nombre no puede estar vacío";

    private final TaskService service;
    private final TextBox input = new TextBox(new TerminalSize(60, 1));
    private final Label message = new Label("");
    private TaskList created;

    public NewListWindow(TaskService service) {
        super(TITLE);
        this.service = service;

        setHints(List.of(Window.Hint.CENTERED));

        message.setForegroundColor(VisualStyle.ERROR);

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(new Label("Nombre de la lista:"));
        content.addComponent(input.withBorder(new RoundedBorder()));
        content.addComponent(new Label("Enter para confirmar · Esc para cancelar"));
        content.addComponent(message);
        setComponent(content);

        setFocusedInteractable(input);
    }

    TaskList getCreatedList() {
        return created;
    }

    String getMessageText() {
        return message.getText();
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter) {
            String name = input.getText().trim();
            if (name.isEmpty()) {
                message.setText(EMPTY_NAME);
                return true;
            }
            created = service.createList(name);
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
