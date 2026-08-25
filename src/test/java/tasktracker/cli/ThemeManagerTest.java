package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import org.junit.jupiter.api.Test;

class ThemeManagerTest {

    @Test
    void startsInLightTheme() {
        ThemeManager manager = new ThemeManager();

        assertFalse(manager.isDark());
        ThemeDefinition definition = manager.current().getDefaultDefinition();
        assertEquals(TextColor.ANSI.BLACK, definition.getNormal().getForeground());
        assertEquals(TextColor.ANSI.WHITE, definition.getNormal().getBackground());
    }

    @Test
    void toggleSwitchesToDarkTheme() {
        ThemeManager manager = new ThemeManager();

        manager.toggle();

        assertTrue(manager.isDark());
        ThemeDefinition definition = manager.current().getDefaultDefinition();
        assertEquals(TextColor.ANSI.WHITE, definition.getNormal().getForeground());
        assertEquals(TextColor.ANSI.BLACK, definition.getNormal().getBackground());
    }

    @Test
    void toggleTwiceReturnsToLightTheme() {
        ThemeManager manager = new ThemeManager();

        manager.toggle();
        manager.toggle();

        assertFalse(manager.isDark());
    }
}
