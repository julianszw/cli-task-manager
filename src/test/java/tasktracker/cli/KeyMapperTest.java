package tasktracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class KeyMapperTest {

    @Test
    void mapsMovementKeys() {
        assertEquals(Key.UP, KeyMapper.fromChar('k'));
        assertEquals(Key.UP, KeyMapper.fromChar('K'));
        assertEquals(Key.DOWN, KeyMapper.fromChar('j'));
        assertEquals(Key.DOWN, KeyMapper.fromChar('J'));
    }

    @Test
    void mapsActionKeys() {
        assertEquals(Key.COMPLETE, KeyMapper.fromChar('c'));
        assertEquals(Key.DELETE, KeyMapper.fromChar('d'));
        assertEquals(Key.PURGE, KeyMapper.fromChar('p'));
        assertEquals(Key.BACK, KeyMapper.fromChar('b'));
        assertEquals(Key.BACK, KeyMapper.fromChar('B'));
        assertEquals(Key.EXIT, KeyMapper.fromChar('q'));
    }

    @Test
    void mapsArrowKeys() {
        assertEquals(Key.UP, KeyMapper.fromArrow('A'));
        assertEquals(Key.DOWN, KeyMapper.fromArrow('B'));
    }

    @Test
    void returnsNullForUnknownChar() {
        assertNull(KeyMapper.fromChar('z'));
        assertNull(KeyMapper.fromArrow('C'));
    }
}
