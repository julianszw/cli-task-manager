package tasktracker.cli;

public interface Command {

    void execute(String[] args, TaskTrackerView view);

    String getName();

    String getDescription();
}
