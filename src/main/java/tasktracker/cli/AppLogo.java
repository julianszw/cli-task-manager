package tasktracker.cli;

import java.util.List;

final class AppLogo {

    private static final String TEXT = "TASK MANAGER";
    private static final int MIN_WIDTH = 12;

    private static final List<String> LINES = buildLines();

    private AppLogo() {
    }

    static List<String> lines() {
        return LINES;
    }

    static int minWidth() {
        return MIN_WIDTH;
    }

    static List<String> fit(int width) {
        if (width < MIN_WIDTH) {
            return List.of();
        }
        return LINES.stream()
                .map(line -> truncate(line, width))
                .toList();
    }

    private static String truncate(String line, int width) {
        if (line.length() <= width) {
            return line;
        }
        return line.substring(0, width);
    }

    private static List<String> buildLines() {
        StringBuilder[] rows = new StringBuilder[5];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new StringBuilder();
        }
        for (int i = 0; i < TEXT.length(); i++) {
            if (i > 0) {
                for (StringBuilder row : rows) {
                    row.append(' ');
                }
            }
            String[] glyph = glyph(TEXT.charAt(i));
            for (int r = 0; r < rows.length; r++) {
                rows[r].append(glyph[r]);
            }
        }
        return List.of(
                rows[0].toString(),
                rows[1].toString(),
                rows[2].toString(),
                rows[3].toString(),
                rows[4].toString());
    }

    private static String[] glyph(char c) {
        return switch (c) {
            case 'T' -> new String[]{"█████", "  █  ", "  █  ", "  █  ", "  █  "};
            case 'A' -> new String[]{" ███ ", "█   █", "█████", "█   █", "█   █"};
            case 'S' -> new String[]{" ████", "█    ", " ███ ", "    █", "████ "};
            case 'K' -> new String[]{"█   █", "█  █ ", "███  ", "█  █ ", "█   █"};
            case 'M' -> new String[]{"█   █", "██ ██", "█ █ █", "█   █", "█   █"};
            case 'N' -> new String[]{"█   █", "██  █", "█ █ █", "█  ██", "█   █"};
            case 'G' -> new String[]{" ███ ", "█   █", "█    ", "█ ███", " ███ "};
            case 'E' -> new String[]{"█████", "█    ", "███  ", "█    ", "█████"};
            case 'R' -> new String[]{"████ ", "█   █", "████ ", "█ █  ", "█  █ "};
            default -> new String[]{"     ", "     ", "     ", "     ", "     "};
        };
    }
}
