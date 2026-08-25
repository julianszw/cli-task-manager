package tasktracker.cli;

public interface TaskTrackerView {

    void showMessage(String message);

    void showTaskList();

    void showAddTask();

    void showCompleteTask();

    void showReopenTask();

    void exit();
}
