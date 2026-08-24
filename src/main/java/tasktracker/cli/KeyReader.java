package tasktracker.cli;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class KeyReader implements KeySource, AutoCloseable {

    private static final int ESCAPE = 27;
    private static final int BRACKET = '[';
    private static final long ESCAPE_TIMEOUT_MILLIS = 50L;

    private final Terminal terminal;
    private final NonBlockingReader reader;

    public KeyReader() throws IOException {
        this(TerminalBuilder.builder().system(true).build());
    }

    KeyReader(Terminal terminal) throws IOException {
        this.terminal = terminal;
        this.reader = terminal.reader();
        terminal.enterRawMode();
    }

    @Override
    public Key readKey() throws IOException {
        int first = reader.read();
        if (first < 0) {
            return Key.EXIT;
        }
        if (first == ESCAPE) {
            return readEscapeSequence();
        }
        Key key = KeyMapper.fromChar((char) first);
        return key != null ? key : readKey();
    }

    private Key readEscapeSequence() throws IOException {
        int next = reader.peek(ESCAPE_TIMEOUT_MILLIS);
        if (next != BRACKET) {
            return Key.EXIT;
        }
        reader.read();
        int direction = reader.read();
        Key key = KeyMapper.fromArrow((char) direction);
        return key != null ? key : readKey();
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }
}
