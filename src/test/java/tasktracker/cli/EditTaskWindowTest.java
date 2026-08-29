package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tasktracker.FakeTaskProvider;
import tasktracker.model.Task;
import tasktracker.service.TaskService;

class EditTaskWindowTest {

    private final TaskService service = new TaskService(new FakeTaskProvider());
    private final String listId = service.createList("Inbox").getId();

    @Test
    void inputIsPreloadedWithCurrentTitle() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        assertEquals("Original", window.inputBox().getText());
    }

    @Test
    void dueInputIsPreloadedWithCurrentDue() {
        Task task = service.addTask(listId, "Original", "2026-08-28");
        EditTaskWindow window = new EditTaskWindow(service, task);

        assertEquals("2026-08-28", window.dueBox().getText());
    }

    @Test
    void dueInputIsPreloadedWithTodayWhenNoDue() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        assertEquals(LocalDate.now().toString(), window.dueBox().getText());
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
    void enterWithDueUpdatesDueDate() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        window.inputBox().setText("");
        type(window, "Original");
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.dueBox().setText("");
        type(window, "2026-08-28");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertTrue(window.isUpdated());
        assertEquals("2026-08-28", service.listTasks(listId).get(0).getDueDate());
    }

    @Test
    void enterWithInvalidDueShowsErrorAndDoesNotUpdate() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task);

        window.inputBox().setText("");
        type(window, "Original");
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.dueBox().setText("");
        type(window, "nope");
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertFalse(window.isUpdated());
        assertTrue(window.getMessageText().contains("yyyy-MM-dd"));
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

    @Test
    void tabWithDatePickerUpdatesDueFromCalendar() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task, initial -> "2026-09-01");

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals("2026-09-01", window.dueBox().getText());
    }

    @Test
    void tabWithDatePickerClearingDueEmptiesField() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task, initial -> "");

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals("", window.dueBox().getText());
    }

    @Test
    void tabWithDatePickerCancelKeepsDueUnchanged() {
        Task task = service.addTask(listId, "Original");
        EditTaskWindow window = new EditTaskWindow(service, task, initial -> null);

        String before = window.dueBox().getText();
        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(before, window.dueBox().getText());
    }

    private void type(EditTaskWindow window, String text) {
        for (char c : text.toCharArray()) {
            window.handleInput(new KeyStroke(c, false, false));
        }
    }
}
