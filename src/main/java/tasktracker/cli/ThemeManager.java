package tasktracker.cli;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;

public final class ThemeManager {

    private static final Theme LIGHT_THEME = buildTheme(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE);
    private static final Theme DARK_THEME = buildTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);

    private boolean dark;

    public boolean isDark() {
        return dark;
    }

    public void toggle() {
        dark = !dark;
    }

    public Theme current() {
        return dark ? DARK_THEME : LIGHT_THEME;
    }

    private static Theme buildTheme(TextColor foreground, TextColor background) {
        SimpleTheme theme = new SimpleTheme(foreground, background);
        theme.getDefaultDefinition().setSelected(background, foreground);
        theme.getDefaultDefinition().setActive(background, foreground);
        return theme;
    }
}
