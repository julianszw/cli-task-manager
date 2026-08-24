package tasktracker.cli;

import java.io.IOException;

public interface KeySource {

    Key readKey() throws IOException;
}
