# Menú principal navegable (TUI-DOS)

## Why

La aplicación arrancaba en un prompt de texto libre (`MainWindow` con `TextBox`)
donde había que escribir los comandos a mano. La spec `main-menu` exige reemplazar
ese prompt por un menú navegable: una lista de opciones que se recorre con las
flechas (o `k`/`j`), se activa con `Enter` o con un atajo de una letra, y no
presenta ningún campo de texto para escribir comandos.

## What Changes

- Menú principal: nuevo `MainMenuWindow` con `ActionListBox` y las opciones
  `Add task`, `List tasks`, `Complete task`, `Reopen task`, `Purge completed`,
  `Help` y `Exit`, cada una con su atajo (`a`, `l`, `c`, `r`, `p`, `h`, `x`).
- Se elimina `MainWindow` (prompt de texto libre con `TextBox`).
- Ventanas auxiliares: `AddTaskWindow` (ingreso de título, con validación de
  título vacío) y `TaskSelectionWindow` (selección de tarea para completar o
  reabrir).
- Vista: `TaskTrackerView` incorpora `showAddTask`, `showCompleteTask` y
  `showReopenTask`; `LanternaTaskTrackerView` abre las ventanas modales y vuelve
  al menú principal.
- Reapertura de tareas: `Task.markPending()` y `TaskService.reopenTask(id)`
  (persiste tras reabrir), además de la tecla `r` en `TaskListWindow`.
- Se extrae `TaskCellRenderer` a su propia clase, compartida por `TaskListWindow`
  y `TaskSelectionWindow`.
- App: construye `MainMenuWindow` en lugar del prompt/dispatcher.

## Impact

- La interacción principal pasa a ser por menú: flechas/`k`/`j` para moverse y
  `Enter` o atajo de letra para activar; ya no hay prompt de texto.
- Se agrega la opción `Reopen task` y la tecla `r` (reabrir) en la vista de tareas.
- La variante CLI básica (`cli-interface`) se conserva: `CommandDispatcher`,
  `CommandRegistry` y los `Command` siguen presentes y testeados.
- Verificación: `mvn test` (93 tests) y `mvn package`.
