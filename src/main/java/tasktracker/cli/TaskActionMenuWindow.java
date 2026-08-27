package tasktracker.cli;

import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;

public class TaskActionMenuWindow extends BasicWindow {

    enum Action {
        COMPLETE("Completar"),
        REOPEN("Reabrir"),
        DELETE("Eliminar"),
        EDIT("Editar");

        private final String label;

        Action(String label) {
            this.label = label;
        }
    }

    private static final String TITLE = "Acciones";

    private final ActionListBox actions = new ActionListBox();
    private Action selected;

    public TaskActionMenuWindow() {
        super(TITLE);
        setHints(List.of(Window.Hint.CENTERED));

        for (Action action : Action.values()) {
            actions.addItem(action.label, () -> {
                selected = action;
                close();
            });
        }

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(actions);
        setComponent(content);

        setFocusedInteractable(actions);
    }

    Action getSelectedAction() {
        return selected;
    }

    int selectedIndex() {
        return actions.getSelectedIndex();
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null) {
                switch (c) {
                    case 'k' -> {
                        moveSelection(-1);
                        return true;
                    }
                    case 'j' -> {
                        moveSelection(1);
                        return true;
                    }
                    default -> {
                    }
                }
            }
        }
        return super.handleInput(key);
    }

    private void moveSelection(int delta) {
        int current = actions.getSelectedIndex();
        int next = current + delta;
        if (next < 0 || next >= actions.getItemCount()) {
            return;
        }
        actions.setSelectedIndex(next);
    }
}
