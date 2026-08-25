package tasktracker.cli;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;

public class MainWindow extends BasicWindow {

    private static final String TITLE = "CLI Task Tracker";
    private static final String PROMPT = "Escribe un comando y pulsa Enter:";
    private static final String HELP = "Comandos: add <título> · list · complete <id> · purge · help · exit";

    private final CommandDispatcher dispatcher;
    private final TaskTrackerView view;
    private final TextBox input = new TextBox(new TerminalSize(60, 1));
    private final Label output = new Label("");

    public MainWindow(CommandDispatcher dispatcher, TaskTrackerView view) {
        super(TITLE);
        this.dispatcher = dispatcher;
        this.view = view;

        setHints(List.of(Window.Hint.FULL_SCREEN));

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(new Label(PROMPT));
        content.addComponent(input.withBorder(Borders.singleLine()));
        content.addComponent(new Label("Resultado:"));
        content.addComponent(output);
        content.addComponent(new EmptySpace());
        content.addComponent(new Label(HELP));
        setComponent(content);

        setFocusedInteractable(input);
    }

    public void setOutput(String message) {
        output.setText(message);
    }

    public void focusInput() {
        setFocusedInteractable(input);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter) {
            dispatcher.dispatch(input.getText(), view);
            input.setText("");
            return true;
        }
        return super.handleInput(key);
    }
}
