package tasktracker.cli;

import java.util.ArrayList;
import java.util.List;

class FakeTaskTrackerView implements TaskTrackerView {

    private final List<String> messages = new ArrayList<>();
    private boolean taskListShown;
    private boolean addTaskShown;
    private boolean completeTaskShown;
    private boolean reopenTaskShown;
    private boolean exited;

    @Override
    public void showMessage(String message) {
        messages.add(message);
    }

    @Override
    public void showTaskList() {
        taskListShown = true;
    }

    @Override
    public void showAddTask() {
        addTaskShown = true;
    }

    @Override
    public void showCompleteTask() {
        completeTaskShown = true;
    }

    @Override
    public void showReopenTask() {
        reopenTaskShown = true;
    }

    @Override
    public void exit() {
        exited = true;
    }

    String lastMessage() {
        return messages.isEmpty() ? "" : messages.get(messages.size() - 1);
    }

    List<String> messages() {
        return messages;
    }

    boolean isTaskListShown() {
        return taskListShown;
    }

    boolean isAddTaskShown() {
        return addTaskShown;
    }

    boolean isCompleteTaskShown() {
        return completeTaskShown;
    }

    boolean isReopenTaskShown() {
        return reopenTaskShown;
    }

    boolean isExited() {
        return exited;
    }
}
