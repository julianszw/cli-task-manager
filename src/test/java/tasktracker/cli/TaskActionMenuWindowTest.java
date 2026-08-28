package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;

class TaskActionMenuWindowTest {

    @Test
    void enterSelectsFirstActionByDefault() {
        TaskActionMenuWindow window = new TaskActionMenuWindow();

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(TaskActionMenuWindow.Action.COMPLETE, window.getSelectedAction());
    }

    @Test
    void jMovesSelectionDown() {
        TaskActionMenuWindow window = new TaskActionMenuWindow();

        window.handleInput(new KeyStroke('j', false, false));

        assertEquals(1, window.selectedIndex());
    }

    @Test
    void kMovesSelectionUp() {
        TaskActionMenuWindow window = new TaskActionMenuWindow();

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke('k', false, false));

        assertEquals(0, window.selectedIndex());
    }

    @Test
    void navigationIsBounded() {
        TaskActionMenuWindow window = new TaskActionMenuWindow();

        window.handleInput(new KeyStroke('k', false, false));
        assertEquals(0, window.selectedIndex());

        for (int i = 0; i < 10; i++) {
            window.handleInput(new KeyStroke('j', false, false));
        }
        assertEquals(TaskActionMenuWindow.Action.values().length - 1, window.selectedIndex());
    }

    @Test
    void enterExecutesSelectedActionAfterNavigation() {
        TaskActionMenuWindow window = new TaskActionMenuWindow();

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(TaskActionMenuWindow.Action.REOPEN, window.getSelectedAction());
    }

    @Test
    void escapeCancelsWithoutSelectingAction() {
        TaskActionMenuWindow window = new TaskActionMenuWindow();

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke(KeyType.Escape));

        assertNull(window.getSelectedAction());
    }

    @Test
    void actionsAreListedInStableOrder() {
        TaskActionMenuWindow.Action[] actions = TaskActionMenuWindow.Action.values();

        assertEquals(5, actions.length);
        assertEquals(TaskActionMenuWindow.Action.COMPLETE, actions[0]);
        assertEquals(TaskActionMenuWindow.Action.REOPEN, actions[1]);
        assertEquals(TaskActionMenuWindow.Action.DELETE, actions[2]);
        assertEquals(TaskActionMenuWindow.Action.EDIT, actions[3]);
        assertEquals(TaskActionMenuWindow.Action.MOVE, actions[4]);
    }
}
