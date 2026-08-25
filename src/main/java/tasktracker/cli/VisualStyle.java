package tasktracker.cli;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;

public final class VisualStyle {

    public static final TextColor BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor FOREGROUND = TextColor.ANSI.WHITE_BRIGHT;
    public static final TextColor DIM = TextColor.ANSI.WHITE;
    public static final TextColor ACCENT = TextColor.ANSI.CYAN;
    public static final TextColor ACCENT_ALT = TextColor.ANSI.MAGENTA;
    public static final TextColor DONE = TextColor.ANSI.GREEN;
    public static final TextColor WARN = TextColor.ANSI.YELLOW;
    public static final TextColor ERROR = TextColor.ANSI.RED;

    private static final Theme THEME = buildTheme();

    private VisualStyle() {
    }

    public static Theme theme() {
        return THEME;
    }

    private static Theme buildTheme() {
        SimpleTheme theme = new SimpleTheme(FOREGROUND, BACKGROUND);
        theme.getDefaultDefinition().setSelected(ACCENT, BACKGROUND);
        theme.getDefaultDefinition().setActive(ACCENT, BACKGROUND);
        return theme;
    }
}
