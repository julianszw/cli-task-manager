package tasktracker.cli;

import java.util.List;

final class ShortcutBar {

    record Shortcut(String key, String label) {
        Shortcut withoutLabel() {
            return new Shortcut(key, "");
        }
    }

    private static final String SEPARATOR = "  ";

    private final List<Shortcut> shortcuts;

    ShortcutBar(List<Shortcut> shortcuts) {
        this.shortcuts = List.copyOf(shortcuts);
    }

    List<Shortcut> visible(int availableWidth) {
        if (availableWidth <= 0) {
            return List.of();
        }
        if (fits(shortcuts, availableWidth)) {
            return shortcuts;
        }
        List<Shortcut> noLabels = shortcuts.stream()
                .map(Shortcut::withoutLabel)
                .toList();
        if (fits(noLabels, availableWidth)) {
            return noLabels;
        }
        for (int start = 1; start < noLabels.size(); start++) {
            List<Shortcut> subset = noLabels.subList(start, noLabels.size());
            if (fits(subset, availableWidth)) {
                return subset;
            }
        }
        return List.of(shortcuts.get(shortcuts.size() - 1).withoutLabel());
    }

    private static boolean fits(List<Shortcut> items, int availableWidth) {
        return measure(items) <= availableWidth;
    }

    private static int measure(List<Shortcut> items) {
        int total = 0;
        for (Shortcut s : items) {
            total += s.key().length();
            if (!s.label().isEmpty()) {
                total += 1 + s.label().length();
            }
        }
        if (items.size() > 1) {
            total += SEPARATOR.length() * (items.size() - 1);
        }
        return total;
    }
}
