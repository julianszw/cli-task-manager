# 029 — Recuperar calendario interactivo en el campo de fecha

## Why
El calendario interactivo (`CalendarWindow`) quedó conectado únicamente a la
acción "Fecha" del menú de acciones. En los campos de fecha de las ventanas de
nueva tarea y edición de tarea, la tecla `Tab` solo movía el foco al `TextBox`
de fecha en lugar de abrir el calendario, contradiciendo la spec `date-selection`
("al posicionarse sobre el campo de fecha, se abre un calendario interactivo").

## What Changes
- Se agrega la seam funcional `DatePicker` (`String pick(LocalDate initial)`)
  para abrir el calendario sin acoplar las ventanas a `WindowBasedTextGUI`.
- `AddTaskWindow` y `EditTaskWindow` aceptan un `DatePicker` opcional; con él,
  `Tab` abre el calendario y vuelca el resultado en el campo de fecha
  (`"yyyy-MM-dd"` para elegir, `""` para quitar, `null` para cancelar).
- Sin `DatePicker` (tests headless / entrada manual), `Tab` conserva el
  comportamiento anterior de enfocar el `TextBox` de fecha.
- `TaskListWindow` inyecta el picker real (`pickDate`), que abre `CalendarWindow`
  con `gui.addWindowAndWait`; la acción "Fecha" (`openTaskDate`) se refactoriza
  para reutilizar `pickDate`.
- Se actualiza el hint de ayuda de ambas ventanas a "Tab para elegir fecha".

## Impact
- Specs: `date-selection` ya describía el comportamiento; no hay delta de spec.
- Tests: nuevos casos para el flujo del calendario en `AddTaskWindowTest` y
  `EditTaskWindowTest` (elegir, quitar, cancelar y fallback manual).
