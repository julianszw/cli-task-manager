# Refactor de la capa de presentación a Lanterna

## Why

La aplicación mezclaba tres mecanismos de presentación: un REPL con `Scanner`,
salida por `System.out` con secuencias ANSI escritas a mano (`TaskTableFormatter`)
y entrada raw con JLine (`KeyReader`). Esto hacía la UI difícil de mantener y de
probar, y alejaba el resultado del estilo de una TUI moderna. Se unifica toda la
presentación sobre Lanterna, un toolkit TUI en Java que aporta ventanas, paneles,
componentes (tabla, caja de texto) y manejo de teclado.

## What Changes

- Dependencias: se agrega `com.googlecode.lanterna:lanterna:3.1.5` y se elimina
  `org.jline:jline` (ya no se usa entrada raw manual).
- Capa `cli`:
  - Se eliminan `Key`, `KeyMapper`, `KeySource`, `KeyReader` (entrada raw JLine),
    `InteractiveTaskBrowser` (loop manual) y `TaskTableFormatter` (ANSI manual).
  - `Command.execute` recibe un `TaskTrackerView` en lugar de escribir a
    `System.out`.
  - Nuevos `TaskTrackerView` (abstracción de UI: `showMessage`, `showTaskList`,
    `exit`), `CommandDispatcher` (parseo y despacho) y `ExitCommand`.
  - `LanternaTaskTrackerView` implementa la vista sobre `MultiWindowTextGUI`.
  - `MainWindow` (prompt de comandos con `TextBox` + `Label` de resultado) y
    `TaskListWindow` (tabla de tareas con `Table` y atajos de teclado).
  - `App` construye terminal/screen/gui y ejecuta el loop con Lanterna.
- Formato: tachado y color por estado pasan a un `TableCellRenderer`
  (`SGR.CROSSED_OUT` + colores ANSI) en `TaskListWindow`.
- Skill: nuevo skill `lanterna` en `.agents/skills/lanterna/SKILL.md`.
- Docs: `project.md` registra Lanterna en el tech stack; specs actualizadas.

## Impact

- Toda la presentación queda en `cli`, sin ANSI manual ni entrada raw propia.
- Los comandos dejan de imprimir a `System.out`; dependen de `TaskTrackerView`.
- Se agrega el comando `exit` para salir de la aplicación.
- `list` sigue iniciando el modo interactivo, ahora como ventana Lanterna.
- Verificación: `mvn test` (47 tests) y `mvn package` en verde.
