# Vista única de tareas (reemplaza el menú principal)

## Why

La aplicación arrancaba en un menú principal (`MainMenuWindow`) con una lista de
opciones separadas para crear, listar, completar, reabrir y purgar. La spec
`interactive-cli` (vista única) exige abrir directamente en la lista de tareas,
sin menú: todas las acciones se ejecutan con atajos de teclado sobre la misma
vista. La spec `main-menu` queda eliminada.

## What Changes

- Se eliminan `MainMenuWindow` y `TaskSelectionWindow` (y sus tests): completar y
  reabrir ya se hacían inline en `TaskListWindow` con `c`/`r`.
- `TaskListWindow` pasa a ser la ventana principal: nueva tecla `a` (crear tarea,
  abre `AddTaskWindow` modal y refresca), se elimina la tecla `b` (volver al
  menú); `q`/`Esc` cierran la aplicación.
- `TaskListWindow` acepta un `WindowBasedTextGUI` (nullable) para poder abrir la
  ventana modal de creación y un mensaje de estado inicial (avisos de arranque).
- `LanternaTaskTrackerView` deja de implementar `TaskTrackerView` y se convierte
  en el lanzador de la vista única (`start(...)`).
- `App` construye directamente la vista de tareas y le pasa los avisos de arranque
  de la persistencia JSON.
- `TaskTrackerView` se recorta a `showMessage`, `showTaskList` y `exit` (los
  métodos `showAddTask`/`showCompleteTask`/`showReopenTask` solo los usaba el
  menú); se ajusta `FakeTaskTrackerView`.
- La ayuda permanente refleja las teclas de la vista única (sin `t`, que llega en
  el cambio `012-dark-theme`).

## Impact

- La interacción principal pasa a ser una única lista de tareas navegable con
  `↑`/`k`, `↓`/`j`, `a`, `c`, `r`, `d`, `p` y `q`/`Esc`.
- La variante CLI básica (`cli-interface`) se conserva: `CommandDispatcher`,
  `CommandRegistry` y los `Command` siguen presentes y testeados.
- Verificación: `mvn test` y `mvn package`.
