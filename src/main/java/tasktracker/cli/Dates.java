package tasktracker.cli;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

final class Dates {

    private Dates() {
    }

    static String today() {
        return LocalDate.now().toString();
    }

    static LocalDate parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
