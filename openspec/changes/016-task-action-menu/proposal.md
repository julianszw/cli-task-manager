# Menú de acciones sobre la tarea seleccionada

## Why

La vista única (`interactive-cli`) permite ejecutar acciones sobre la tarea
seleccionada solo mediante atajos de teclado (`c`, `r`, `d`, `a`). El spec
`task-action-menu` define un menú contextual que se abre con `Enter` y permite
navegar entre las acciones (completar, reabrir, eliminar y editar) y ejecutarlas,
complementando los atajos y añadiendo la acción de editar el título (hasta ahora
imposible desde la interfaz, pese a que `task-management` ya exigía "Actualizar
título de tarea").

## What Changes

- `Task`: el campo `title` deja de ser `final` y se añade `rename(String)`.
- `TaskService`: se añade `renameTask(long id, String title)`, que valida el
  título no vacío y lanza `TaskNotFoundException` si la tarea no existe
  (implementa el requirement "Actualizar título de tarea" de `task-management`).
- Nuevo `TaskActionMenuWindow`: ventana modal centrada que lista las acciones en
  orden estable (completar, reabrir, eliminar, editar). Navegación con `↑`/`k` y
  `↓`/`j`, ejecución con `Enter` y cancelación con `Esc`.
- Nuevo `EditTaskWindow`: ventana modal con campo precargado con el título actual;
  `Enter` valida no vacío y renombra, `Esc` cancela, y el título vacío muestra error.
- `TaskListWindow`: `Enter` abre el menú de acciones (no hace nada sin tareas o sin
  GUI); al ejecutar una acción se aplica y se redibuja la vista. Mientras el menú
  está abierto, los atajos de la lista quedan deshabilitados (ventana modal).
- `TaskViewRenderer`: se añade el atajo `Enter → acciones` a la barra de ayuda
  permanente (según `interactive-cli`).

## Impact

- Nueva capacidad `task-action-menu` (spec ya definida en `openspec/specs/`).
- Se completa el requirement "Actualizar título de tarea" de `task-management`.
- No cambia la persistencia ni el comportamiento de los atajos existentes fuera
  del menú.
- Verificación: `mvn test` y `mvn package`.
