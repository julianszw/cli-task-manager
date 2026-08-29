package tasktracker.cli;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.provider.ProviderException;
import tasktracker.service.TaskService;

public class TaskListWindow extends BasicWindow {

    private static final String TITLE = "Tareas";
    private static final String NO_TASKS = "No hay tareas cargadas";
    private static final String NO_COMPLETED_TO_PURGE = "No hay tareas completadas para eliminar";
    private static final String NO_OTHER_LIST = "No hay otra lista a la que mover la tarea";

    private final TaskService service;
    private final WindowBasedTextGUI gui;
    private final TaskViewComponent view = new TaskViewComponent();
    private final List<TaskList> lists = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();
    private int activeIndex;
    private int selected;
    private String status = "";
    private MessageKind kind = MessageKind.INFO;
    private boolean hideEmptyLists = false;

    public TaskListWindow(TaskService service) {
        this(service, null);
    }

    public TaskListWindow(TaskService service, WindowBasedTextGUI gui) {
        super(TITLE);
        this.service = service;
        this.gui = gui;
        lists.addAll(service.listLists());

        setHints(List.of(Window.Hint.FULL_SCREEN));

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(view,
                LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow));
        setComponent(content);

        refresh();
    }

    void setStatus(String message) {
        setMessage(message, MessageKind.INFO);
    }

    void setWarning(String message) {
        setMessage(message, MessageKind.WARN);
    }

    String getStatusText() {
        return status;
    }

    int activeListIndex() {
        return activeIndex;
    }

    String activeListId() {
        return activeList().getId();
    }

    int taskCount() {
        return tasks.size();
    }

    int zoomLevel() {
        return service.getZoomLevel();
    }

    private void setMessage(String message, MessageKind kind) {
        this.status = message == null ? "" : message;
        this.kind = kind;
        render();
    }

    private void refresh() {
        tasks.clear();
        if (!lists.isEmpty()) {
            tasks.addAll(service.listTasks(activeList().getId()));
        }
        if (selected >= tasks.size()) {
            selected = Math.max(0, tasks.size() - 1);
        }
        if (tasks.isEmpty()) {
            status = NO_TASKS;
            kind = MessageKind.INFO;
        } else {
            status = "";
            kind = MessageKind.INFO;
        }
        render();
    }

    private void render() {
        view.setZoom(service.getZoomLevel());
        view.setModel(tasks, selected, status, kind, listIndicator());
    }

    private TaskList activeList() {
        return lists.get(activeIndex);
    }

    String listIndicator() {
        if (lists.isEmpty()) {
            return "";
        }
        return activeList().getTitle() + " (" + service.providerName() + ") · " + (activeIndex + 1) + "/" + lists.size();
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.isCtrlDown() && key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null) {
                switch (c) {
                    case '=' -> {
                        zoomIn();
                        return true;
                    }
                    case '-' -> {
                        zoomOut();
                        return true;
                    }
                    case '0' -> {
                        zoomReset();
                        return true;
                    }
                    default -> {
                    }
                }
            }
        }
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null) {
                switch (c) {
                    case 'h' -> {
                        hideEmptyLists = !hideEmptyLists;
                        refresh();
                        return true;
                    }
                    case 'k' -> {
                        moveUp();
                        return true;
                    }
                    case 'j' -> {
                        moveDown();
                        return true;
                    }
                    case 'a' -> {
                        openAddTask();
                        return true;
                    }
                    case 'n' -> {
                        openNewList();
                        return true;
                    }
                    case 'c' -> {
                        completeSelected();
                        return true;
                    }
                    case 'r' -> {
                        reopenSelected();
                        return true;
                    }
                    case 'd' -> {
                        deleteSelected();
                        return true;
                    }
                    case 'p' -> {
                        purgeCompleted();
                        return true;
                    }
                    case 'q' -> {
                        requestExit();
                        return true;
                    }
                    default -> {
                    }
                }
            }
        } else if (key.getKeyType() == KeyType.Tab) {
            nextList();
            return true;
        } else if (key.getKeyType() == KeyType.ReverseTab) {
            previousList();
            return true;
        } else if (key.getKeyType() == KeyType.Enter) {
            openActionMenu();
            return true;
        } else if (key.getKeyType() == KeyType.ArrowUp) {
            moveUp();
            return true;
        } else if (key.getKeyType() == KeyType.ArrowDown) {
            moveDown();
            return true;
        } else if (key.getKeyType() == KeyType.Escape) {
            requestExit();
            return true;
        }
        return super.handleInput(key);
    }

    private void moveUp() {
        if (!tasks.isEmpty() && selected > 0) {
            selected--;
            render();
        }
    }

    private void moveDown() {
        if (!tasks.isEmpty() && selected < tasks.size() - 1) {
            selected++;
            render();
        }
    }

    private void zoomIn() {
        service.setZoomLevel(service.getZoomLevel() + 1);
        render();
    }

    private void zoomOut() {
        service.setZoomLevel(service.getZoomLevel() - 1);
        render();
    }

    private void zoomReset() {
        service.setZoomLevel(TaskService.DEFAULT_ZOOM);
        render();
    }

    private void nextList() {
        if (lists.size() > 1) {
            int startIndex = activeIndex;
            do {
                activeIndex = (activeIndex + 1) % lists.size();
                if (!hideEmptyLists || !service.listTasks(activeList().getId()).isEmpty()) {
                    break;
                }
            } while (activeIndex != startIndex);
            selected = 0;
            refresh();
        }
    }

    private void previousList() {
        if (lists.size() > 1) {
            int startIndex = activeIndex;
            do {
                activeIndex = (activeIndex - 1 + lists.size()) % lists.size();
                if (!hideEmptyLists || !service.listTasks(activeList().getId()).isEmpty()) {
                    break;
                }
            } while (activeIndex != startIndex);
            selected = 0;
            refresh();
        }
    }

    private void openAddTask() {
        if (gui == null || lists.isEmpty()) {
            return;
        }
        gui.addWindowAndWait(new AddTaskWindow(service, activeList().getId()));
        refresh();
    }

    private void openNewList() {
        if (gui == null) {
            return;
        }
        NewListWindow window = new NewListWindow(service);
        gui.addWindowAndWait(window);
        if (window.getCreatedList() != null) {
            lists.clear();
            lists.addAll(service.listLists());
            activeIndex = lists.size() - 1;
            selected = 0;
            refresh();
        }
    }

    private void openActionMenu() {
        if (tasks.isEmpty() || gui == null) {
            return;
        }
        TaskActionMenuWindow menu = new TaskActionMenuWindow();
        gui.addWindowAndWait(menu);
        TaskActionMenuWindow.Action action = menu.getSelectedAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case COMPLETE -> completeSelected();
            case REOPEN -> reopenSelected();
            case DELETE -> deleteSelected();
            case EDIT -> openEditTask();
            case FECHA -> openTaskDate();
            case MOVE -> openMoveTask();
        }
    }

    private void openMoveTask() {
        if (gui == null || selected < 0 || selected >= tasks.size()) {
            return;
        }
        Task task = tasks.get(selected);
        List<TaskList> targets = lists.stream()
                .filter(list -> !list.getId().equals(task.getListId()))
                .toList();
        if (targets.isEmpty()) {
            setStatus(NO_OTHER_LIST);
            return;
        }
        OptionMenuWindow menu = new OptionMenuWindow(
                "Mover a otra lista", targets.stream().map(TaskList::getTitle).toList());
        gui.addWindowAndWait(menu);
        int index = menu.selectedIndex();
        if (index < 0) {
            return;
        }
        try {
            service.moveTask(task.getId(), targets.get(index).getId());
        } catch (ProviderException e) {
            setWarning(e.getMessage());
        }
        refresh();
    }

    private void requestExit() {
        if (gui == null) {
            return;
        }
        OptionMenuWindow confirm = new OptionMenuWindow(
                "¿Estás seguro?", List.of("Sí", "No"), 1);
        gui.addWindowAndWait(confirm);
        if (confirm.selectedIndex() == 0) {
            close();
        }
    }

    private void openEditTask() {
        if (gui == null || selected < 0 || selected >= tasks.size()) {
            return;
        }
        gui.addWindowAndWait(new EditTaskWindow(service, tasks.get(selected)));
        refresh();
    }

    private void openTaskDate() {
        if (gui == null || selected < 0 || selected >= tasks.size()) {
            return;
        }
        Task task = tasks.get(selected);
        LocalDate initial = task.getDueDate() != null ? Dates.parse(task.getDueDate()) : LocalDate.now();
        if (initial == null) initial = LocalDate.now();
        String picked = pickDate(initial);
        if (picked != null) {
            try {
                service.setTaskDue(task.getId(), picked);
            } catch (ProviderException e) {
                setWarning(e.getMessage());
            }
        }
        refresh();
    }

    private String pickDate(LocalDate initial) {
        CalendarWindow window = new CalendarWindow(initial);
        gui.addWindowAndWait(window);
        if (window.isCleared()) {
            return "";
        }
        return window.getSelectedDate() == null ? null : window.getSelectedDate().toString();
    }

    private void completeSelected() {
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        try {
            service.completeTask(tasks.get(selected).getId());
        } catch (ProviderException e) {
            setWarning(e.getMessage());
        }
        refresh();
    }

    private void reopenSelected() {
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        try {
            service.reopenTask(tasks.get(selected).getId());
        } catch (ProviderException e) {
            setWarning(e.getMessage());
        }
        refresh();
    }

    private void deleteSelected() {
        if (selected < 0 || selected >= tasks.size()) {
            return;
        }
        try {
            service.deleteTask(tasks.get(selected).getId());
        } catch (ProviderException e) {
            setWarning(e.getMessage());
        }
        refresh();
    }

    private void purgeCompleted() {
        if (lists.isEmpty()) {
            return;
        }
        try {
            List<Task> removed = service.purgeCompletedTasks(activeList().getId());
            refresh();
            if (removed.isEmpty()) {
                setStatus(NO_COMPLETED_TO_PURGE);
            } else {
                setStatus("Tareas completadas eliminadas: " + removed.size());
            }
        } catch (ProviderException e) {
            setWarning(e.getMessage());
            refresh();
        }
    }
}
