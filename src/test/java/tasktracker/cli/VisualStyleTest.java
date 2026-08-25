package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import org.junit.jupiter.api.Test;

class VisualStyleTest {

    @Test
    void themeIsAlwaysDark() {
        ThemeDefinition definition = VisualStyle.theme().getDefaultDefinition();

        assertEquals(TextColor.ANSI.BLACK, definition.getNormal().getBackground());
        assertEquals(TextColor.ANSI.WHITE_BRIGHT, definition.getNormal().getForeground());
    }

    @Test
    void themeIsStableAcrossCalls() {
        assertEquals(VisualStyle.theme(), VisualStyle.theme());
    }
}
