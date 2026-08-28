# Mover tareas entre listas, formato/orden de completadas y confirmación de salida

## Why

Tres mejoras sobre la vista única (`interactive-cli`) y el menú de acciones
(`task-action-menu`) que quedaron especificadas pero sin implementar:

1. No era posible cambiar una tarea de lista sin borrarla y recrearla. El spec
   `task-management` exige una operación "mover tarea a otra lista", accesible desde
   el menú de acciones como una acción propia ("mover"), separada de "editar".
2. Las tareas completadas solo se atenuaban; el usuario pide además tacharlas, poner
   el texto en gris y moverlas automáticamente al final de la lista (agrupando
   pendientes primero y completadas al final, según `output-formatting`).
3. Salir con `q`/`Esc` cerraba la aplicación sin avisar; se añade un diálogo de
   confirmación "¿estás seguro?" (sí/no) con "no" por defecto, según `interactive-cli`.

## What Changes

- `TaskService`: nueva operación `moveTask(taskId, targetListId)` (valida lista y
  tarea, es no-op si es la misma lista) y `listTasks(listId)` ordena de forma estable
  por estado (pendientes primero, completadas al final).
- `TaskActionMenuWindow`: nueva acción `MOVER` en el menú (orden: completar, reabrir,
  eliminar, editar, mover). "editar" mantiene el comportamiento previo: abre
  directamente el campo de título.
- `OptionMenuWindow` (nuevo, package-private): menú modal genérico sobre
  `ActionListBox` con navegación `j`/`k`/flechas, `Enter` para seleccionar y `Esc`
  para cancelar. Se reutiliza para el selector de lista destino y el diálogo de
  confirmación.
- `TaskListWindow`: nueva acción "mover" con selector de lista destino (excluye la
  actual; si no hay otra lista muestra un mensaje); `q`/`Esc` piden confirmación
  antes de cerrar.
- `TaskViewRenderer`: las tareas completadas muestran el ícono `✓` verde y el título
  tachado (`SGR.CROSSED_OUT`) en gris.

## Impact

- Se implementan los requisitos añadidos a `task-management` (mover tarea),
  `output-formatting` (formato y orden de completadas), `visual-style` (gris) e
  `interactive-cli` (confirmación de salida) y `task-action-menu` (acción mover).
- El orden de `listTasks` cambia: las completadas ya no conservan su posición original
  en el listado (se agrupan al final).
- Verificación: `mvn test` y `mvn package`.
