# Zoom visual global de la interfaz

## Why

La vista única de tareas (`interactive-cli`) no permitía ajustar el tamaño de la
interfaz. El nuevo spec `ui-zoom` define la posibilidad de hacer zoom visual global:
cinco niveles discretos (`-2` a `+2`, con `0` por defecto), controlados con
`Ctrl+=` (aumentar), `Ctrl+-` (disminuir) y `Ctrl+0` (restablecer), y persistidos
entre sesiones.

## What Changes

- `TaskService`: constantes `MIN_ZOOM`/`MAX_ZOOM`/`DEFAULT_ZOOM` y operaciones
  `getZoomLevel()` / `setZoomLevel(int)` que acotan el nivel al rango y delegan en
  el repositorio.
- `TaskRepository` (interfaz) e `InMemoryTaskRepository`: métodos `getZoom()` /
  `setZoom(int)`.
- `JsonStoreCodec`: el documento raíz incluye el campo `zoom` (entero). El
  `encode` lo escribe y el `decode` lo lee, con valor por defecto `0` cuando el
  campo está ausente (compatibilidad con archivos anteriores).
- `JsonTaskRepository`: persiste y recarga el nivel de zoom junto con el estado.
- `TaskListWindow`: atajos `Ctrl+=`, `Ctrl+-` y `Ctrl+0` que ajustan el nivel y
  redibujan la vista.
- `TaskViewComponent` / `TaskViewRenderer`: el nivel de zoom se aplica al layout de
  la lista (el alto de fila escala con el zoom positivo) y el logo ASCII se omite en
  zoom negativo (vista compacta).

## Impact

- Se implementa la capacidad `ui-zoom`.
- Se amplía el formato del archivo JSON (`json-persistence`) con el campo `zoom`; los
  archivos existentes sin el campo se cargan con nivel `0`.
- Verificación: `mvn test` y `mvn package`.
