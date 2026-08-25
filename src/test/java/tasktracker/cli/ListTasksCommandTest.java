package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ListTasksCommandTest {

    @Test
    void executeShowsTaskList() {
        FakeTaskTrackerView view = new FakeTaskTrackerView();

        new ListTasksCommand().execute(new String[0], view);

        assertTrue(view.isTaskListShown());
    }
}
