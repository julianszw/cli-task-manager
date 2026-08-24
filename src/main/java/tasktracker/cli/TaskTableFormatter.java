package tasktracker.cli;

import java.util.List;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

public class TaskTableFormatter {

    private static final String RESET = "\033[0m";
    private static final String STRIKETHROUGH_ON = "\033[9m";
    private static final String PENDING_COLOR = "\033[33m";
    private static final String COMPLETED_COLOR = "\033[32m";
    private static final String SELECTED_STYLE = "\033[7m";

    private static final String HEADER_ID = "ID";
    private static final String HEADER_STATUS = "ESTADO";
    private static final String HEADER_TITLE = "TÍTULO";
    private static final String SEPARATOR = " | ";

    public String render(List<Task> tasks) {
        return render(tasks, -1);
    }

    public String render(List<Task> tasks, int selectedIndex) {
        if (tasks.isEmpty()) {
            return "";
        }

        int idWidth = HEADER_ID.length();
        int statusWidth = HEADER_STATUS.length();
        int titleWidth = HEADER_TITLE.length();
        for (Task task : tasks) {
            idWidth = Math.max(idWidth, Long.toString(task.getId()).length());
            statusWidth = Math.max(statusWidth, task.getStatus().name().length());
            titleWidth = Math.max(titleWidth, task.getTitle().length());
        }

        StringBuilder table = new StringBuilder();
        table.append(pad(HEADER_ID, idWidth)).append(SEPARATOR)
                .append(pad(HEADER_STATUS, statusWidth)).append(SEPARATOR)
                .append(pad(HEADER_TITLE, titleWidth));

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String row = pad(Long.toString(task.getId()), idWidth) + SEPARATOR
                    + pad(task.getStatus().name(), statusWidth) + SEPARATOR
                    + pad(task.getTitle(), titleWidth);

            table.append(System.lineSeparator()).append(style(row, task, i == selectedIndex));
        }

        return table.toString();
    }

    private String style(String row, Task task, boolean selected) {
        boolean completed = task.getStatus() == TaskStatus.COMPLETED;

        StringBuilder styled = new StringBuilder();
        styled.append(completed ? COMPLETED_COLOR : PENDING_COLOR);
        if (completed) {
            styled.append(STRIKETHROUGH_ON);
        }
        if (selected) {
            styled.append(SELECTED_STYLE);
        }
        styled.append(row).append(RESET);
        return styled.toString();
    }

    private String pad(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        return text + " ".repeat(width - text.length());
    }
}
