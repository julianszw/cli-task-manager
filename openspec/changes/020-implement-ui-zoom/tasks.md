# Tasks

- [x] `TaskRepository` e `InMemoryTaskRepository`: métodos `getZoom()` / `setZoom(int)`
- [x] `JsonStoreCodec`: campo `zoom` en `encode`/`decode` (default `0` si falta)
- [x] `JsonTaskRepository`: persistir y recargar el nivel de zoom
- [x] `TaskService`: constantes de rango y `getZoomLevel()` / `setZoomLevel(int)` con clamp
- [x] `TaskViewComponent` / `TaskViewRenderer`: aplicar zoom al layout de la lista y al logo
- [x] `TaskListWindow`: atajos `Ctrl+=`, `Ctrl+-`, `Ctrl+0` con redibujado
- [x] Tests (service, codec, repository, list window)
- [x] Actualizar spec `json-persistence` (campo `zoom`)
- [x] Compilar y ejecutar tests (`mvn test`, `mvn package`)
