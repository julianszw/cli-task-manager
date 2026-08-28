package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CalendarWindowTest {

    @Test
    void enterSelectsInitialDate() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 8, 28), window.getSelectedDate());
        assertFalse(window.isCleared());
    }

    @Test
    void arrowRightMovesSelectionOneDay() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.ArrowRight));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 8, 29), window.getSelectedDate());
    }

    @Test
    void arrowLeftMovesSelectionBackOneDay() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.ArrowLeft));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 8, 27), window.getSelectedDate());
    }

    @Test
    void arrowUpMovesSelectionBackOneWeek() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.ArrowUp));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 8, 21), window.getSelectedDate());
    }

    @Test
    void arrowDownMovesSelectionForwardOneWeek() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.ArrowDown));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 9, 4), window.getSelectedDate());
    }

    @Test
    void tabMovesToNextMonth() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.Tab));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 9, 28), window.getSelectedDate());
    }

    @Test
    void reverseTabMovesToPreviousMonth() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.ReverseTab));
        window.handleInput(new KeyStroke(KeyType.Enter));

        assertEquals(LocalDate.of(2026, 7, 28), window.getSelectedDate());
    }

    @Test
    void escapeCancelsWithoutSelecting() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke(KeyType.Escape));

        assertNull(window.getSelectedDate());
        assertFalse(window.isCleared());
    }

    @Test
    void dClearsDate() {
        CalendarWindow window = new CalendarWindow(LocalDate.of(2026, 8, 28));

        window.handleInput(new KeyStroke('d', false, false));

        assertTrue(window.isCleared());
        assertNull(window.getSelectedDate());
    }
}
