package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class AddTaskWindowTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());

    @Test
    void enterWithTitleCreatesTask() {
        AddTaskWindow window = new AddTaskWindow(service);

        type(window, "comprar leche");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals("comprar leche", window.getCreatedTask().getTitle());
        assertEquals(1, service.listTasks().size());
    }

    @Test
    void enterWithBlankTitleShowsErrorAndCreatesNothing() {
        AddTaskWindow window = new AddTaskWindow(service);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedTask());
        assertTrue(window.getMessageText().contains("no puede estar vacío"));
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void whitespaceOnlyTitleIsRejected() {
        AddTaskWindow window = new AddTaskWindow(service);

        type(window, "   ");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertNull(window.getCreatedTask());
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void escapeCancelsWithoutCreatingTask() {
        AddTaskWindow window = new AddTaskWindow(service);

        type(window, "tarea");
        window.handleInput(new KeyStroke(KeyType.Escape));

        assertNull(window.getCreatedTask());
        assertTrue(service.listTasks().isEmpty());
    }

    private void type(AddTaskWindow window, String text) {
        for (char c : text.toCharArray()) {
            window.handleInput(new KeyStroke(c, false, false));
        }
    }
}
