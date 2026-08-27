package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class EditTaskWindowTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());
    private final long listId = service.createList("Inbox").getId();

    @Test
    void inputIsPreloadedWithCurrentTitle() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        assertEquals("Original", window.inputBox().getText());
    }

    @Test
    void enterRenamesTask() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        window.inputBox().setText("");
        type(window, "Nuevo");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertTrue(window.isUpdated());
        assertEquals("Nuevo", service.listTasks(listId).get(0).getTitle());
    }

    @Test
    void enterWithBlankTitleShowsErrorAndDoesNotRename() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        window.inputBox().setText("   ");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertFalse(window.isUpdated());
        assertTrue(window.getMessageText().contains("no puede estar vacío"));
        assertEquals("Original", service.listTasks(listId).get(0).getTitle());
    }

    @Test
    void escapeCancelsWithoutRenaming() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        window.inputBox().setText("Nuevo");
        window.handleInput(new KeyStroke(KeyType.Escape));

        assertFalse(window.isUpdated());
        assertEquals("Original", service.listTasks(listId).get(0).getTitle());
    }

    private void type(EditTaskWindow window, String text) {
        for (char c : text.toCharArray()) {
            window.handleInput(new KeyStroke(c, false, false));
        }
    }
}
