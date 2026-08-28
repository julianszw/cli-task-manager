package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;
import tasktracker.FakeTaskProvider;
import tasktracker.service.TaskService;

class NewListWindowTest {

    private final TaskService service = new TaskService(new FakeTaskProvider());

    @Test
    void enterWithNameCreatesList() {
        NewListWindow window = new NewListWindow(service);

        type(window, "Trabajo");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals("Trabajo", window.getCreatedList().getTitle());
        assertEquals(1, service.listLists().size());
    }

    @Test
    void enterWithBlankNameShowsErrorAndCreatesNothing() {
        NewListWindow window = new NewListWindow(service);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedList());
        assertTrue(window.getMessageText().contains("no puede estar vacío"));
        assertTrue(service.listLists().isEmpty());
    }

    @Test
    void whitespaceOnlyNameIsRejected() {
        NewListWindow window = new NewListWindow(service);

        type(window, "   ");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedList());
        assertTrue(service.listLists().isEmpty());
    }

    @Test
    void escapeCancelsWithoutCreatingList() {
        NewListWindow window = new NewListWindow(service);

        type(window, "trabajo");
        window.handleInput(new KeyStroke(KeyType.Escape));

        assertNull(window.getCreatedList());
        assertTrue(service.listLists().isEmpty());
    }

    private void type(NewListWindow window, String text) {
        for (char c : text.toCharArray()) {
            window.handleInput(new KeyStroke(c, false, false));
        }
    }
}
