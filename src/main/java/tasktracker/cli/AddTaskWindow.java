package tasktracker.cli;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.time.LocalDate;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.provider.ProviderException;
import tasktracker.service.TaskService;

public class AddTaskWindow extends BasicWindow {

    private static final String TITLE = "Nueva tarea";
    private static final String EMPTY_TITLE = "⚠ El título no puede estar vacío";
    private static final String TITLE_LABEL = "Título de la tarea:";
    private static final String DUE_LABEL = "Fecha (yyyy-MM-dd, opcional):";
    private static final String LIST_LABEL = "Lista:";
    private static final String FOCUS_PREFIX = "▸ ";

    enum Field {
        TITLE, DUE, LIST
    }

    private final TaskService service;
    private final DatePicker datePicker;
    private final List<TaskList> lists;
    private final TextBox input = new TextBox(new TerminalSize(60, 1));
    private final TextBox dueInput = new TextBox(new TerminalSize(60, 1));
    private final Label titleLabel = new Label(TITLE_LABEL);
    private final Label dueLabel = new Label(DUE_LABEL);
    private final Label listTitleLabel = new Label(LIST_LABEL);
    private final Label listLabel = new Label("");
    private final Label message = new Label("");
    private int listIndex;
    private Field focus = Field.TITLE;
    private Task created;

    public AddTaskWindow(TaskService service, String listId) {
        this(service, listId, null);
    }

    public AddTaskWindow(TaskService service, String listId, DatePicker datePicker) {
        super(TITLE);
        this.service = service;
        this.datePicker = datePicker;
        this.lists = service.listLists();
        this.listIndex = indexOfList(listId);

        setHints(List.of(Window.Hint.CENTERED));

        message.setForegroundColor(VisualStyle.ERROR);
        dueInput.setText(Dates.today());

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(titleLabel);
        content.addComponent(input.withBorder(new RoundedBorder()));
        content.addComponent(dueLabel);
        content.addComponent(dueInput.withBorder(new RoundedBorder()));
        content.addComponent(listTitleLabel);
        content.addComponent(listLabel.withBorder(new RoundedBorder()));
        content.addComponent(new Label("Enter confirmar · Tab mover/ciclar · Shift+Tab atrás · Esc cancelar"));
        content.addComponent(message);
        setComponent(content);

        updateListLabel();
        setFocus(Field.TITLE);
    }

    public Task getCreatedTask() {
        return created;
    }

    String getMessageText() {
        return message.getText();
    }

    TextBox inputBox() {
        return input;
    }

    TextBox dueBox() {
        return dueInput;
    }

    Field focus() {
        return focus;
    }

    String listLabelText() {
        return listLabel.getText();
    }

    String selectedListId() {
        if (lists.isEmpty()) {
            return null;
        }
        return lists.get(listIndex).getId();
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Tab) {
            switch (focus) {
                case TITLE -> enterDue();
                case DUE -> setFocus(Field.LIST);
                case LIST -> cycleList(1);
            }
            return true;
        }
        if (key.getKeyType() == KeyType.ReverseTab) {
            switch (focus) {
                case TITLE -> setFocus(Field.LIST);
                case DUE -> setFocus(Field.TITLE);
                case LIST -> enterDue();
            }
            return true;
        }
        if (key.getKeyType() == KeyType.Enter) {
            confirm();
            return true;
        }
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Character && focus == Field.LIST) {
            return true;
        }
        return super.handleInput(key);
    }

    private void enterDue() {
        setFocus(Field.DUE);
        openCalendar();
    }

    private void openCalendar() {
        if (datePicker == null) {
            return;
        }
        LocalDate initial = Dates.parse(dueInput.getText());
        if (initial == null) {
            initial = LocalDate.now();
        }
        String picked = datePicker.pick(initial);
        if (picked != null) {
            dueInput.setText(picked);
        }
    }

    private void cycleList(int delta) {
        if (lists.isEmpty()) {
            return;
        }
        listIndex = (listIndex + delta + lists.size()) % lists.size();
        updateListLabel();
    }

    private void confirm() {
        String title = input.getText().trim();
        if (title.isEmpty()) {
            message.setText(EMPTY_TITLE);
            return;
        }
        try {
            created = service.addTask(selectedListId(), title, dueInput.getText());
            close();
        } catch (IllegalArgumentException | ProviderException e) {
            message.setText(e.getMessage());
        }
    }

    private int indexOfList(String listId) {
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId().equals(listId)) {
                return i;
            }
        }
        return 0;
    }

    private void updateListLabel() {
        if (lists.isEmpty()) {
            listLabel.setText("");
            return;
        }
        listLabel.setText(lists.get(listIndex).getTitle());
    }

    private void setFocus(Field next) {
        this.focus = next;
        updateLabel(titleLabel, TITLE_LABEL, focus == Field.TITLE);
        updateLabel(dueLabel, DUE_LABEL, focus == Field.DUE);
        updateLabel(listTitleLabel, LIST_LABEL, focus == Field.LIST);
        if (focus == Field.TITLE) {
            setFocusedInteractable(input);
        } else if (focus == Field.DUE) {
            setFocusedInteractable(dueInput);
        }
    }

    private static void updateLabel(Label label, String base, boolean focused) {
        label.setText(focused ? FOCUS_PREFIX + base : base);
        label.setForegroundColor(focused ? VisualStyle.ACCENT : VisualStyle.DIM);
    }
}
