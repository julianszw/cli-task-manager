package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.repository.InMemoryTaskRepository;
import tasktracker.service.TaskService;

class MainMenuWindowTest {

    private final TaskService service = new TaskService(new InMemoryTaskRepository());

    @Test
    void firstOptionIsSelectedByDefault() {
        MainMenuWindow window = new MainMenuWindow(new FakeTaskTrackerView(), service);

        assertEquals(0, window.getSelectedIndex());
    }

    @Test
    void shortcutAShowsAddTask() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();
        MainMenuWindow window = new MainMenuWindow(view, service);

        window.handleInput(new KeyStroke('a', false, false));

        assertTrue(view.isAddTaskShown());
    }

    @Test
    void shortcutLShowsTaskList() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();
        MainMenuWindow window = new MainMenuWindow(view, service);

        window.handleInput(new KeyStroke('l', false, false));

        assertTrue(view.isTaskListShown());
    }

    @Test
    void shortcutCShowsCompleteTask() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();
        MainMenuWindow window = new MainMenuWindow(view, service);

        window.handleInput(new KeyStroke('c', false, false));

        assertTrue(view.isCompleteTaskShown());
    }

    @Test
    void shortcutRShowsReopenTask() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();
        MainMenuWindow window = new MainMenuWindow(view, service);

        window.handleInput(new KeyStroke('r', false, false));

        assertTrue(view.isReopenTaskShown());
    }

    @Test
    void shortcutXExits() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();
        MainMenuWindow window = new MainMenuWindow(view, service);

        window.handleInput(new KeyStroke('x', false, false));

        assertTrue(view.isExited());
    }

    @Test
    void shortcutPPurgesCompletedTasks() {
        Task completed = service.addTask("Completada");
        service.completeTask(completed.getId());
        service.addTask("Pendiente");
        MainMenuWindow window = new MainMenuWindow(new FakeTaskTrackerView(), service);

        window.handleInput(new KeyStroke('p', false, false));

        assertEquals(1, service.listTasks().size());
        assertEquals("Pendiente", service.listTasks().get(0).getTitle());
        assertTrue(window.getOutputText().contains("eliminadas"));
    }

    @Test
    void shortcutHShowsHelpWithOptions() {
        MainMenuWindow window = new MainMenuWindow(new FakeTaskTrackerView(), service);

        window.handleInput(new KeyStroke('h', false, false));

        assertTrue(window.getOutputText().contains("Add task"));
        assertTrue(window.getOutputText().contains("Exit"));
    }

    @Test
    void jAndKMoveSelection() {
        MainMenuWindow window = new MainMenuWindow(new FakeTaskTrackerView(), service);

        window.handleInput(new KeyStroke('j', false, false));
        assertEquals(1, window.getSelectedIndex());

        window.handleInput(new KeyStroke('k', false, false));
        assertEquals(0, window.getSelectedIndex());
    }

    @Test
    void selectionIsClampedAtMenuBounds() {
        MainMenuWindow window = new MainMenuWindow(new FakeTaskTrackerView(), service);

        window.handleInput(new KeyStroke('k', false, false));
        assertEquals(0, window.getSelectedIndex());

        for (int i = 0; i < 10; i++) {
            window.handleInput(new KeyStroke('j', false, false));
        }
        assertEquals(6, window.getSelectedIndex());
    }

    @Test
    void enterActivatesSelectedOption() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();
        MainMenuWindow window = new MainMenuWindow(view, service);

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertTrue(view.isAddTaskShown());
    }
}
