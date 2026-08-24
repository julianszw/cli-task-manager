package tasktracker.cli;

public interface Command {

    void execute(String[] args);

    String getName();

    String getDescription();
}
