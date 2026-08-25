package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ShortcutBarTest {

    private final ShortcutBar bar = new ShortcutBar(List.of(
            new ShortcutBar.Shortcut("↑/k", "subir"),
            new ShortcutBar.Shortcut("↓/j", "bajar"),
            new ShortcutBar.Shortcut("q/Esc", "salir")));

    @Test
    void wideTerminalShowsAllWithLabels() {
        List<ShortcutBar.Shortcut> visible = bar.visible(200);

        assertEquals(3, visible.size());
        assertEquals("subir", visible.get(0).label());
        assertEquals("salir", visible.get(2).label());
    }

    @Test
    void narrowTerminalDropsLabelsBeforeShortcuts() {
        List<ShortcutBar.Shortcut> visible = bar.visible(20);

        assertEquals(3, visible.size());
        assertTrue(visible.stream().allMatch(s -> s.label().isEmpty()));
    }

    @Test
    void evenNarrowerDropsLowPriorityShortcuts() {
        List<ShortcutBar.Shortcut> visible = bar.visible(10);

        assertEquals(2, visible.size());
        assertEquals("↓/j", visible.get(0).key());
        assertEquals("q/Esc", visible.get(1).key());
    }

    @Test
    void alwaysKeepsTheLastShortcut() {
        List<ShortcutBar.Shortcut> visible = bar.visible(2);

        assertEquals(1, visible.size());
        assertEquals("q/Esc", visible.get(0).key());
        assertTrue(visible.get(0).label().isEmpty());
    }

    @Test
    void zeroWidthShowsNothing() {
        assertTrue(bar.visible(0).isEmpty());
    }
}
