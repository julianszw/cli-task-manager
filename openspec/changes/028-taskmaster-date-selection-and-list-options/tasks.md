# Tareas

- [x] `TaskService#normalizeDue` convierte `yyyy-MM-dd` a RFC 3339 (`T00:00:00.000Z`)
- [x] `TaskProvider#providerName()` + implementaciones (`GoogleTasksProvider`, `FakeTaskProvider`)
- [x] Logo ASCII "TaskMaster" (`AppLogo`) y chip de título "TaskMaster" (`TaskViewRenderer`)
- [x] Título de la ventana de la terminal "TaskMaster" (`App`)
- [x] `CalendarWindow`: calendario interactivo navegable (días, semanas, meses)
- [x] Autocompletado: campo de fecha precargado con hoy (`AddTaskWindow`, `EditTaskWindow`)
- [x] Acción "Fecha" del menú abre el calendario (elimina `TaskDateWindow`)
- [x] Atajo `h` para ocultar listas vacías en la navegación (`TaskListWindow`)
- [x] Indicador de lista con proveedor atenuado (`TaskViewComponent`/`TaskViewRenderer`)
- [x] Tests: actualizados + nuevos (service, cli, calendario, listas)
- [x] `mvn test` en verde (135 tests)
