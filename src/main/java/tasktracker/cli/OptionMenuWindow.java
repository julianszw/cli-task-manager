package tasktracker.cli;

import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.List;

final class OptionMenuWindow extends BasicWindow {

    private final ActionListBox options = new ActionListBox();
    private int selected = -1;

    OptionMenuWindow(String title, List<String> items) {
        this(title, items, 0);
    }

    OptionMenuWindow(String title, List<String> items, int defaultIndex) {
        super(title);
        setHints(List.of(Window.Hint.CENTERED));

        for (int i = 0; i < items.size(); i++) {
            int index = i;
            options.addItem(items.get(i), () -> {
                selected = index;
                close();
            });
        }

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(options);
        setComponent(content);

        options.setSelectedIndex(defaultIndex);
        setFocusedInteractable(options);
    }

    int selectedIndex() {
        return selected;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null) {
                switch (c) {
                    case 'k' -> {
                        moveSelection(-1);
                        return true;
                    }
                    case 'j' -> {
                        moveSelection(1);
                        return true;
                    }
                    default -> {
                    }
                }
            }
        }
        return super.handleInput(key);
    }

    private void moveSelection(int delta) {
        int current = options.getSelectedIndex();
        int next = current + delta;
        if (next < 0 || next >= options.getItemCount()) {
            return;
        }
        options.setSelectedIndex(next);
    }
}
