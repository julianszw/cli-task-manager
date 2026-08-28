package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tasktracker.FakeTaskProvider;
import tasktracker.service.TaskService;

class AddTaskWindowTest {

    private final TaskService service = new TaskService(new FakeTaskProvider());
    private final String listId = service.createList("Inbox").getId();

    @Test
    void enterWithTitleCreatesTask() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "comprar leche");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals("comprar leche", window.getCreatedTask().getTitle());
        assertEquals(1, service.listTasks(listId).size());
    }

    @Test
    void createdTaskBelongsToActiveList() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "tarea");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(listId, window.getCreatedTask().getListId());
    }

    @Test
    void enterWithTitleAndDueCreatesTaskWithDate() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "entregar");
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.dueBox().setText("");
        type(window, "2026-08-28");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals("2026-08-28", window.getCreatedTask().getDueDate());
        assertEquals(1, service.listTasks(listId).size());
    }

    @Test
    void enterWithInvalidDueShowsErrorAndCreatesNothing() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "entregar");
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.dueBox().setText("");
        type(window, "nope");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedTask());
        assertTrue(window.getMessageText().contains("yyyy-MM-dd"));
        assertTrue(service.listTasks(listId).isEmpty());
    }

    @Test
    void dueInputIsPreloadedWithToday() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        assertEquals(LocalDate.now().toString(), window.dueBox().getText());
    }

    @Test
    void enterWithTitleOnlyCreatesTaskWithTodayDate() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "tarea");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.now().toString(), window.getCreatedTask().getDueDate());
    }

    @Test
    void enterWithBlankTitleShowsErrorAndCreatesNothing() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedTask());
        assertTrue(window.getMessageText().contains("no puede estar vacío"));
        assertTrue(service.listTasks(listId).isEmpty());
    }

    @Test
    void whitespaceOnlyTitleIsRejected() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "   ");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedTask());
        assertTrue(service.listTasks(listId).isEmpty());
    }

    @Test
    void escapeCancelsWithoutCreatingTask() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "tarea");
        window.handleInput(new KeyStroke(KeyType.Escape));

        assertNull(window.getCreatedTask());
        assertTrue(service.listTasks(listId).isEmpty());
    }

    private void type(AddTaskWindow window, String text) {
        for (char c : text.toCharArray()) {
            window.handleInput(new KeyStroke(c, false, false));
        }
    }
}
