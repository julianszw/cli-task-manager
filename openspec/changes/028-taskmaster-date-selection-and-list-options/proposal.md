# 028 — TaskMaster, selección de fecha y opciones de listas

## Why
- El nombre "Task Manager" no convence; se rebautiza la aplicación como "TaskMaster".
- Crear una tarea con fecha `yyyy-MM-dd` fallaba con "invalid argument" porque
  Google Tasks espera un timestamp RFC 3339 completo en el campo `due`.
- El campo de fecha estaba vacío y sin asistencia: se quiere autocompletar con la
  fecha de hoy y ofrecer un calendario interactivo para elegirla.
- Se quiere ocultar las listas vacías de la navegación (manteniéndolas como destino
  al mover tareas) y mostrar el proveedor junto al nombre de la lista, anticipando la
  futura integración de múltiples servicios de tareas.

## What Changes
- Renombrado a TaskMaster: logo ASCII, chip de título de la barra de estado y título
  de la ventana de la terminal (`DefaultTerminalFactory#setTerminalEmulatorTitle`).
- Fecha: `TaskService#normalizeDue` convierte `yyyy-MM-dd` a timestamp RFC 3339
  (`T00:00:00.000Z`); se agrega `CalendarWindow` (calendario interactivo navegable);
  los campos de fecha se precargan con la fecha de hoy.
- Listas: la tecla `h` alterna ocultar las listas vacías en la navegación con
  `Tab`/`Shift+Tab`; el atajo se refleja en la barra de ayuda.
- Proveedor: se agrega `TaskProvider#providerName()` y el indicador de lista muestra
  "Nombre (Proveedor)" con el proveedor atenuado.
- Se elimina `TaskDateWindow`; la acción "Fecha" del menú abre el calendario
  directamente (elegir, quitar con `d`, cancelar con `Esc`).

## Impact
- Specs nuevas: `date-selection`, `hide-empty-lists`, `provider-indicator`.
- Specs actualizadas: `task-provider`, `output-formatting`, `app-logo`,
  `visual-style`, `interactive-cli`, `task-action-menu`.
- La fecha interna de `Task#getDue()` pasa a almacenar el timestamp completo;
  `Task#getDueDate()` sigue exponiendo la fecha sola (`yyyy-MM-dd`).
