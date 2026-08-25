---
name: lanterna
description: Best practices for building terminal UIs (TUI) in Java with Lanterna. Covers Terminal/Screen/TextGUI, windows, panels and layouts, components (Table, TextBox, ActionListBox), KeyStroke input handling, colors/SGR styling, themes, and headless testing. Use when creating or refactoring a Java CLI/TUI, rendering tables or interactive lists, handling keyboard input in a terminal, or writing tests for Lanterna GUI code.
allowed-tools: Read, Write, Bash, Glob, Grep
---

# Lanterna TUI Best Practices

Your goal is to build clean, testable terminal UIs in Java using Lanterna
(`com.googlecode.lanterna:lanterna`, 3.1.x). Lanterna is a full TUI toolkit:
screens, windows, panels, components, themes, and key-stroke handling. It is
the closest Java equivalent to Go's Bubble Tea/Lip Gloss stack.

## When to use

- Replacing a `Scanner`-based REPL or raw-ANSI/JLine UI with a real windowed TUI.
- Rendering tables, lists, forms, or dialogs in a terminal.
- Handling keyboard navigation (arrows, vim-style keys, shortcuts).

## Maven setup

```xml
<dependency>
  <groupId>com.googlecode.lanterna</groupId>
  <artifactId>lanterna</artifactId>
  <version>3.1.5</version>
</dependency>
```

## Core building blocks

| Concept      | Type                                             | Role                                                        |
| ------------ | ------------------------------------------------ | ----------------------------------------------------------- |
| Terminal     | `Terminal` (from `DefaultTerminalFactory`)        | Low-level backend; reads keys, writes chars/colors.         |
| Screen       | `Screen` (`TerminalScreen` or `VirtualScreen`)    | Buffered drawing surface; diffs frames before redrawing.    |
| GUI          | `TextGUI` (`MultiWindowTextGUI`, `SingleWindowTextGUI`) | Window manager + input dispatch.                       |
| Window       | `BasicWindow` (subclass)                          | Top-level container with title, border, components.         |
| Component    | `Panel`, `Label`, `TextBox`, `Table`, `ActionListBox`, `Button`, `CheckBoxList`, `ComboBox`, `Separator`, `EmptySpace` | Widgets placed inside windows/panels. |
| Key          | `KeyStroke`, `KeyType`                            | Typed key/combination delivered to the focused component.   |

### Bootstrapping a full-screen app

```java
Terminal terminal = new DefaultTerminalFactory().createTerminal();
Screen screen = new TerminalScreen(terminal);
screen.startScreen();
try {
    // Default thread factory = SameTextGUIThread: runs the event/update loop
    // on the current thread; addWindowAndWait blocks until the window closes.
    TextGUI gui = new MultiWindowTextGUI(screen);

    BasicWindow window = new BasicWindow("Título");
    window.setHints(List.of(Window.Hint.FULL_SCREEN));
    // ... build component tree, setComponent(...)
    gui.addWindowAndWait(window);     // runs the loop until window closes
} finally {
    screen.stopScreen();              // always restore terminal state
}
```

- `new MultiWindowTextGUI(screen)` uses `SameTextGUIThread` (no background
  thread); `addWindowAndWait`/`waitForWindowToClose` pump events on the current
  thread. Use `new SeparateTextGUIThread.Factory()` for a dedicated thread.

## Panels and layouts

Panels nest components and control layout via a `LayoutManager`:

```java
Panel root = new Panel(new LinearLayout(Direction.VERTICAL));   // stack top→bottom
Panel grid = new Panel(new GridLayout(2));                       // fixed columns
Panel bordered = new Panel(new BorderLayout());                  // N/S/E/W/CENTER
```

Common helpers:
- `panel.addComponent(component)` — append with the current layout.
- `component.withBorder(Borders.singleLine("Título"))` — add a box border.
- `EmptySpace` / `Separator` — spacing and horizontal rules.

## Input handling

Two layers:

1. **Component-level**: focused components handle arrows/tabs themselves
   (`Table`, `ActionListBox`, `TextBox`).
2. **Window-level**: override `handleInput(KeyStroke)` or register a
   `WindowListener.onInput` to intercept keys before/after the component.

```java
@Override
public boolean handleInput(KeyStroke key) {
    if (key.getKeyType() == KeyType.Character) {
        switch (key.getCharacter()) {
            case 'q' -> { close(); return true; }
        }
    }
    return super.handleInput(key);
}
```

`KeyType` covers `ArrowUp/Down/Left/Right`, `Enter`, `Escape`, `Tab`,
`Backspace`, `Delete`, `Home`, `End`, `F1..F12`, `Character`, etc.

## Styling: colors and SGR

- Colors: `TextColor.ANSI.RED`, `.GREEN`, `.YELLOW`, `.CYAN`, `.WHITE`, `.BLACK`,
  plus `.Indexed(...)` / `.RGB(...)`.
- Styles (Select Graphic Rendition): `SGR.BOLD`, `SGR.REVERSE`,
  `SGR.UNDERLINE`, `SGR.CROSSED_OUT` (strikethrough), `SGR.BLINK`, `SGR.BORDERED`.

Apply styling per-cell/row via a custom renderer (e.g. `TableCellRenderer` for
tables), or per-character with `TextGUIGraphics.setForegroundColor(...)` /
`putString(...)`.

```java
graphics.setForegroundColor(TextColor.ANSI.GREEN);
graphics.enableModifiers(SGR.CROSSED_OUT);
graphics.putString(col, row, text);
```

- Reuse a single `Theme`/`DefaultTheme` across the GUI instead of hardcoding
  escape sequences.
- Centralize palette choices in a small `Theme` subclass or a constants class.

## Tables

`Table<String>` renders columnar data and manages selection:

```java
Table<String> table = new Table<>("ID", "ESTADO", "TÍTULO");
table.getTableModel().addRow("1", "PENDING", "Comprar leche");
table.setSelectAction(() -> { /* Enter on selected row */ });
table.setSelectedRow(0);
```

Style rows with a custom `TableCellRenderer<String>` so you can color the
status column or strikethrough completed rows. Keep a parallel `List<Task>`
indexed by row when you need the underlying domain object.

## Testing

Do NOT create a real terminal in tests: on Linux, `DefaultTerminalFactory`
builds a `UnixTerminal` that runs `stty` against `/dev/tty` and fails without a
real TTY. Instead:

1. **Keep GUI-agnostic logic out of the GUI.** Parsing, dispatching, and
   formatting live in plain classes (e.g. a `CommandDispatcher`, a `*Formatter`)
   that are unit-tested without Lanterna.
2. **Drive windows via `handleInput(KeyStroke)` directly.** Construct a window
   with a `TaskService` and feed it `new KeyStroke('c', false, false)` or
   `new KeyStroke(KeyType.Escape)` to assert key→action routing and resulting
   state — no terminal, no screen, no GUI needed:

```java
TaskListWindow window = new TaskListWindow(service);
window.handleInput(new KeyStroke('c', false, false)); // complete selected
assertEquals(COMPLETED, service.listTasks().get(0).getStatus());
```

3. Keep a tiny GUI seam (`TaskTrackerView` interface) so commands depend on an
   abstraction, not on Lanterna — test them with a fake view.

## Pitfalls

- `stopScreen()` in `finally` — otherwise the terminal stays in raw mode.
- One GUI/thread per process; don't create multiple `TextGUI` instances.
- Prefer `MultiWindowTextGUI` + `addWindowAndWait` for modal flows instead of
  hand-rolled loops.
- `Window.Hint` is a plain class, not an enum: use `List.of(Window.Hint.FULL_SCREEN)`,
  not `EnumSet.of(...)`.
- Never mix raw `System.out`/ANSI prints with an active `Screen` — it corrupts
  the frame buffer. Route all output through components.
- On terminal resize, Lanterna fires `ResizeListener`; call
  `screen.doResizeIfNecessary()` when driving the screen manually.

## References

- Lanterna GitHub: https://github.com/mabe02/lanterna
- Getting started: https://github.com/mabe02/lanterna/wiki/Using-the-Screen-layer
- GUI overview: https://github.com/mabe02/lanterna/wiki/Using-the-GUI-layer
