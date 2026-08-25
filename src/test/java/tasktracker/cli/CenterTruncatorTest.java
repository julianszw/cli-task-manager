package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CenterTruncatorTest {

    @Test
    void shortTextIsUnchanged() {
        assertEquals("abc", CenterTruncator.truncate("abc", 5));
    }

    @Test
    void exactWidthIsUnchanged() {
        assertEquals("abcde", CenterTruncator.truncate("abcde", 5));
    }

    @Test
    void longTextKeepsStartAndEnd() {
        String result = CenterTruncator.truncate("abcdefghij", 5);

        assertEquals(5, result.length());
        assertEquals("ab…ij", result);
    }

    @Test
    void nullReturnsEmpty() {
        assertEquals("", CenterTruncator.truncate(null, 5));
    }

    @Test
    void zeroOrNegativeReturnsEmpty() {
        assertEquals("", CenterTruncator.truncate("abc", 0));
        assertEquals("", CenterTruncator.truncate("abc", -1));
    }
}
