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
    private final long listId = service.createList("Inbox").getId();

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
