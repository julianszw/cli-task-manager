package tasktracker.cli;

public final class KeyMapper {

    private KeyMapper() {
    }

    public static Key fromChar(char c) {
        return switch (c) {
            case 'k', 'K' -> Key.UP;
            case 'j', 'J' -> Key.DOWN;
            case 'c', 'C' -> Key.COMPLETE;
            case 'd', 'D' -> Key.DELETE;
            case 'p', 'P' -> Key.PURGE;
            case 'q', 'Q' -> Key.EXIT;
            default -> null;
        };
    }

    public static Key fromArrow(char c) {
        return switch (c) {
            case 'A' -> Key.UP;
            case 'B' -> Key.DOWN;
            default -> null;
        };
    }
}
