package tasktracker.cli;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.table.DefaultTableCellRenderer;
import com.googlecode.lanterna.gui2.table.Table;
import java.util.List;
import java.util.function.Supplier;
import tasktracker.model.Task;

final class TaskCellRenderer extends DefaultTableCellRenderer<String> {

    private final List<Task> tasks;
    private final Supplier<Boolean> isDark;

    TaskCellRenderer(List<Task> tasks, Supplier<Boolean> isDark) {
        this.tasks = tasks;
        this.isDark = isDark;
    }

    @Override
    protected void applyStyle(Table<String> table, String cell, int columnIndex, int rowIndex,
            boolean isSelected, TextGUIGraphics graphics) {
        super.applyStyle(table, cell, columnIndex, rowIndex, isSelected, graphics);
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            Task task = tasks.get(rowIndex);
            if (task.isCompleted()) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN);
                graphics.enableModifiers(SGR.CROSSED_OUT);
            } else {
                graphics.setForegroundColor(
                        isDark.get() ? TextColor.ANSI.YELLOW : TextColor.ANSI.BLUE);
            }
        }
    }
}
