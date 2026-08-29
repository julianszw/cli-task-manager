# 030 — Campo de lista en la ventana de nueva tarea

## Why
Al crear una tarea desde la vista única, la tarea quedaba siempre asociada a la
lista activa. No había forma de indicar, desde la propia ventana de "Nueva
tarea", que la tarea debía crearse en una lista distinta de la actual. Para eso
había que crear la tarea y luego moverla.

## What Changes
- `AddTaskWindow` gana un tercer campo "Lista" que muestra la lista destino,
  por defecto la lista activa, y permite elegir otra con `Tab` de forma cíclica.
- Se introduce un foco explícito de tres campos (`TITLE → DUE → LIST`) con
  navegación `Tab`/`Shift+Tab` (con wrap) y resaltado visual del campo activo
  (prefijo `▸` + color de acento).
- Al enfocar el campo de fecha se abre el calendario (según `date-selection`).
- Al confirmar, la tarea se crea en la lista destino seleccionada
  (puede ser distinta de la activa), usando `TaskService.addTask(listId, ...)`.
- El campo de lista recorre todas las listas, incluidas las vacías/ocultas
  (`hide-empty-lists`).

## Impact
- Specs: nueva capacidad `add-task-list-field`.
- Código: solo `cli` (`AddTaskWindow`). Sin cambios en `service`/`provider`.
- Tests: nuevos casos en `AddTaskWindowTest` (foco, ciclado de lista, creación
  en lista elegida, tipeo ignorado en el campo lista).
