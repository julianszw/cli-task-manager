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

    @Test
    void tabWithDatePickerUpdatesDueFromCalendar() {
        AddTaskWindow window = new AddTaskWindow(service, listId, initial -> "2026-09-01");

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals("2026-09-01", window.dueBox().getText());
    }

    @Test
    void tabWithDatePickerClearingDueEmptiesField() {
        AddTaskWindow window = new AddTaskWindow(service, listId, initial -> "");

        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals("", window.dueBox().getText());
    }

    @Test
    void tabWithDatePickerCancelKeepsDueUnchanged() {
        AddTaskWindow window = new AddTaskWindow(service, listId, initial -> null);

        String before = window.dueBox().getText();
        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(before, window.dueBox().getText());
    }

    @Test
    void tabWithoutDatePickerStillFocusesDueField() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.dueBox().setText("");
        type(window, "2026-08-28");

        assertEquals("2026-08-28", window.dueBox().getText());
    }

    @Test
    void listFieldShowsActiveListByDefault() {
        service.createList("Trabajo");
        AddTaskWindow window = new AddTaskWindow(service, listId);

        assertEquals("Inbox", window.listLabelText());
    }

    @Test
    void focusStartsOnTitle() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        assertEquals(AddTaskWindow.Field.TITLE, window.focus());
    }

    @Test
    void tabMovesFocusFromTitleToDueToToList() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Tab));
        assertEquals(AddTaskWindow.Field.DUE, window.focus());

        window.handleInput(new KeyStroke(KeyType.Tab));
        assertEquals(AddTaskWindow.Field.LIST, window.focus());
    }

    @Test
    void tabCyclesListSelection() {
        String work = service.createList("Trabajo").getId();
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(work, window.selectedListId());
        assertEquals("Trabajo", window.listLabelText());
    }

    @Test
    void tabCyclesListWrapsAround() {
        service.createList("Trabajo");
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));

        assertEquals(listId, window.selectedListId());
        assertEquals("Inbox", window.listLabelText());
    }

    @Test
    void shiftTabFromListGoesToDueWithoutChangingList() {
        service.createList("Trabajo");
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        String before = window.selectedListId();

        window.handleInput(new KeyStroke(KeyType.ReverseTab));

        assertEquals(AddTaskWindow.Field.DUE, window.focus());
        assertEquals(before, window.selectedListId());
    }

    @Test
    void shiftTabFromTitleWrapsToList() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.ReverseTab));

        assertEquals(AddTaskWindow.Field.LIST, window.focus());
    }

    @Test
    void confirmCreatesTaskInSelectedList() {
        String work = service.createList("Trabajo").getId();
        AddTaskWindow window = new AddTaskWindow(service, listId);

        type(window, "tarea");
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(work, window.getCreatedTask().getListId());
        assertEquals(1, service.listTasks(work).size());
        assertTrue(service.listTasks(listId).isEmpty());
    }

    @Test
    void typingIsIgnoredWhileOnListField() {
        AddTaskWindow window = new AddTaskWindow(service, listId);

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Tab));
        type(window, "x");

        assertEquals("", window.inputBox().getText());
    }

    private void type(AddTaskWindow window, String text) {
        for (char c : text.toCharArray()) {
            window.handleInput(new KeyStroke(c, false, false));
        }
    }
}
