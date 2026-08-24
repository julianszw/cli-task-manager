# Modo de navegación interactiva para `list`

## Why

El comando `list` imprimía un listado estático y no permitía actuar sobre las
tareas sin salir del comando. Se busca que `list` inicie un modo interactivo
donde el usuario pueda recorrer, completar, eliminar y purgar tareas con teclas.

## What Changes

- Dependencia: `org.jline:jline` para leer teclas individuales (modo raw).
- Dominio: borrado individual de tareas (`TaskRepository.removeById`,
  `TaskService.deleteTask`) con su spec en `task-management`.
- CLI:
  - `Key` (acciones) y `KeyMapper` (mapeo de teclas → acción, puro y testeable).
  - `KeySource` (abstracción de entrada) y `KeyReader` (implementación JLine).
  - `InteractiveTaskBrowser` (loop TUI: render + selección + acciones).
  - `ListTasksCommand` lanza el navegador interactivo.
- Specs: nueva capacidad `interactive-cli`; actualiza `cli-interface`
  (el comando `list` ahora inicia el modo interactivo).
- `project.md`: se registra JLine en el tech stack.
- Tests: `KeyMapperTest`, `InteractiveTaskBrowserTest`, casos de `deleteTask` y
  `removeById`.

## Impact

- `list` ya no imprime un listado estático: entra en modo raw de terminal
  (JLine) y se sale con `q`/`Esc`.
- `TaskRepository` gana `removeById`, por lo que implementaciones futuras deben
  soportarlo.
- Borrado individual disponible solo en el modo interactivo (no hay comando
  REPL `delete`).
- Verificación: `mvn test` en verde.
