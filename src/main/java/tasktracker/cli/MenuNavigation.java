package tasktracker.cli;

final class MenuNavigation {

    private MenuNavigation() {
    }

    static int cycle(int selected, int delta, int count) {
        if (count == 0) {
            return selected;
        }
        return ((selected + delta) % count + count) % count;
    }
}
