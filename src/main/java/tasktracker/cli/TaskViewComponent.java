package tasktracker.cli;

import com.googlecode.lanterna.gui2.AbstractComponent;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import java.util.ArrayList;
import java.util.List;
import tasktracker.model.Task;

final class TaskViewComponent extends AbstractComponent<TaskViewComponent> {

    private final List<Task> tasks = new ArrayList<>();
    private int selected;
    private String message = "";
    private MessageKind kind = MessageKind.INFO;

    void setModel(List<Task> tasks, int selected, String message, MessageKind kind) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        this.selected = selected;
        this.message = message == null ? "" : message;
        this.kind = kind == null ? MessageKind.INFO : kind;
        invalidate();
    }

    List<Task> tasks() {
        return tasks;
    }

    int selected() {
        return selected;
    }

    String message() {
        return message;
    }

    MessageKind kind() {
        return kind;
    }

    @Override
    protected ComponentRenderer<TaskViewComponent> createDefaultRenderer() {
        return new TaskViewRenderer();
    }
}
