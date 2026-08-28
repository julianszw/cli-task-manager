package tasktracker.cli;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbstractComponent;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarWindow extends BasicWindow {

    private static final List<String> DOW = List.of("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do");

    private LocalDate selected;
    private LocalDate result;
    private boolean cleared;
    private final CalendarGrid grid = new CalendarGrid();

    public CalendarWindow(LocalDate initial) {
        super("Fecha de vencimiento");
        setHints(List.of(Window.Hint.CENTERED));
        this.selected = initial;
        grid.setSelected(selected);
        setComponent(grid);
    }

    LocalDate getSelectedDate() {
        return result;
    }

    boolean isCleared() {
        return cleared;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Enter) {
            result = selected;
            close();
            return true;
        }
        if (key.getKeyType() == KeyType.Character) {
            Character c = key.getCharacter();
            if (c != null && c == 'd') {
                cleared = true;
                close();
                return true;
            }
        }
        if (key.getKeyType() == KeyType.ArrowLeft) {
            moveSelection(selected.minusDays(1));
            return true;
        }
        if (key.getKeyType() == KeyType.ArrowRight) {
            moveSelection(selected.plusDays(1));
            return true;
        }
        if (key.getKeyType() == KeyType.ArrowUp) {
            moveSelection(selected.minusDays(7));
            return true;
        }
        if (key.getKeyType() == KeyType.ArrowDown) {
            moveSelection(selected.plusDays(7));
            return true;
        }
        if (key.getKeyType() == KeyType.Tab) {
            moveSelection(selected.plusMonths(1));
            return true;
        }
        if (key.getKeyType() == KeyType.ReverseTab) {
            moveSelection(selected.minusMonths(1));
            return true;
        }
        return super.handleInput(key);
    }

    private void moveSelection(LocalDate next) {
        selected = next;
        grid.setSelected(selected);
    }

    private static final class CalendarGrid extends AbstractComponent<CalendarGrid> {

        private static final String HELP = "Enter elegir · d quitar · Esc salir";

        private LocalDate selected = LocalDate.now();

        void setSelected(LocalDate selected) {
            this.selected = selected;
            invalidate();
        }

        @Override
        protected ComponentRenderer<CalendarGrid> createDefaultRenderer() {
            return new ComponentRenderer<>() {
                @Override
                public TerminalSize getPreferredSize(CalendarGrid component) {
                    return new TerminalSize(24, 9);
                }

                @Override
                public void drawComponent(TextGUIGraphics g, CalendarGrid component) {
                    g.setBackgroundColor(VisualStyle.BACKGROUND);
                    g.setForegroundColor(VisualStyle.FOREGROUND);
                    g.fill(' ');

                    int cols = g.getSize().getColumns();
                    LocalDate date = component.selected;
                    YearMonth month = YearMonth.from(date);

                    int row = 0;
                    g.setForegroundColor(VisualStyle.ACCENT);
                    g.enableModifiers(SGR.BOLD);
                    g.putString(0, row++, month.getMonth().getDisplayName(TextStyle.FULL, new Locale("es"))
                            + " " + month.getYear());
                    g.clearModifiers();

                    g.setForegroundColor(VisualStyle.DIM);
                    g.putString(0, row++, String.join(" ", DOW));

                    LocalDate first = month.atDay(1);
                    int offset = first.getDayOfWeek().getValue() - 1;
                    int days = month.lengthOfMonth();
                    int highlightPos = month.equals(YearMonth.from(date))
                            ? date.getDayOfMonth() - 1 + offset
                            : -1;

                    for (int week = 0; week < 6; week++) {
                        StringBuilder sb = new StringBuilder();
                        for (int dow = 0; dow < 7; dow++) {
                            int day = week * 7 + dow - offset + 1;
                            sb.append(day >= 1 && day <= days ? String.format("%3d", day) : "   ");
                        }
                        g.setForegroundColor(VisualStyle.FOREGROUND);
                        g.putString(0, row, sb.toString());
                        if (highlightPos >= 0 && highlightPos / 7 == week) {
                            int col = (highlightPos % 7) * 3;
                            g.setForegroundColor(VisualStyle.ACCENT);
                            g.enableModifiers(SGR.BOLD);
                            g.putString(col, row, String.format("%3d", date.getDayOfMonth()));
                            g.clearModifiers();
                        }
                        row++;
                    }

                    g.setForegroundColor(VisualStyle.DIM);
                    g.putString(0, row, HELP.substring(0, Math.min(HELP.length(), cols)));
                }
            };
        }
    }
}
