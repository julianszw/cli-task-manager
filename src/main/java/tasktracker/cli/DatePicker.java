package tasktracker.cli;

import java.time.LocalDate;

@FunctionalInterface
public interface DatePicker {

    String pick(LocalDate initial);
}
