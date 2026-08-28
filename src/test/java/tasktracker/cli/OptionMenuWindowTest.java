package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;
import org.junit.jupiter.api.Test;

class OptionMenuWindowTest {

    private static final List<String> ITEMS = List.of("Uno", "Dos", "Tres");

    @Test
    void enterSelectsFirstItemByDefault() {
        OptionMenuWindow window = new OptionMenuWindow("Título", ITEMS);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(0, window.selectedIndex());
    }

    @Test
    void defaultIndexIsSelectedOnOpen() {
        OptionMenuWindow window = new OptionMenuWindow("Título", ITEMS, 1);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(1, window.selectedIndex());
    }

    @Test
    void jMovesSelectionDown() {
        OptionMenuWindow window = new OptionMenuWindow("Título", ITEMS);

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(1, window.selectedIndex());
    }

    @Test
    void kMovesSelectionUp() {
        OptionMenuWindow window = new OptionMenuWindow("Título", ITEMS);

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke('k', false, false));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(0, window.selectedIndex());
    }

    @Test
    void navigationIsBounded() {
        OptionMenuWindow window = new OptionMenuWindow("Título", ITEMS);

        window.handleInput(new KeyStroke('k', false, false));
        for (int i = 0; i < 10; i++) {
            window.handleInput(new KeyStroke('j', false, false));
        }
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(ITEMS.size() - 1, window.selectedIndex());
    }

    @Test
    void escapeCancelsWithoutSelecting() {
        OptionMenuWindow window = new OptionMenuWindow("Título", ITEMS);

        window.handleInput(new KeyStroke('j', false, false));
        window.handleInput(new KeyStroke(KeyType.Escape));

        assertEquals(-1, window.selectedIndex());
    }
}
