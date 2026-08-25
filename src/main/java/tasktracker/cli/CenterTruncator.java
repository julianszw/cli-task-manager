package tasktracker.cli;

final class CenterTruncator {

    private static final String ELLIPSIS = "…";

    private CenterTruncator() {
    }

    static String truncate(String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        if (maxWidth <= 0) {
            return "";
        }
        if (text.length() <= maxWidth) {
            return text;
        }
        if (maxWidth == 1) {
            return ELLIPSIS;
        }
        int start = maxWidth / 2;
        int end = maxWidth - start - ELLIPSIS.length();
        return text.substring(0, start) + ELLIPSIS + text.substring(text.length() - end);
    }
}
