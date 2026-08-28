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
    private String listIndicator = "";
    private int zoom;

    void setModel(List<Task> tasks, int selected, String message, MessageKind kind, String listIndicator) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        this.selected = selected;
        this.message = message == null ? "" : message;
        this.kind = kind == null ? MessageKind.INFO : kind;
        this.listIndicator = listIndicator == null ? "" : listIndicator;
        invalidate();
    }

    void setZoom(int zoom) {
        this.zoom = zoom;
        invalidate();
    }

    int zoom() {
        return zoom;
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

    String listIndicator() {
        return listIndicator;
    }

    @Override
    protected ComponentRenderer<TaskViewComponent> createDefaultRenderer() {
        return new TaskViewRenderer();
    }
}
