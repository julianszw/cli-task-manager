# 027 — Google Tasks como única fuente de datos

## Why
La aplicación operaba sobre un archivo JSON local con sincronización bidireccional
contra Google Tasks. Ese modelo (doble fuente de datos + motor de sync) es complejo y
limita la evolución a otros proveedores. Se decide transformar la aplicación en una
interfaz que ejecuta directamente los métodos de la Google Tasks API, eliminando la
persistencia local y dejando una abstracción de proveedor para sumar Microsoft TO DO
u otros en el futuro.

## What Changes
- Se elimina la persistencia local (JSON) y el repositorio en memoria persistente:
  `repository` (`TaskRepository`, `InMemoryTaskRepository`, `JsonTaskRepository`,
  `JsonStoreCodec`, `JsonParseException`) y todo el motor de sincronización `sync`
  (`TaskSyncService`, `SyncState`, `SyncStateStore`, `SyncResult`, `SyncException`,
  `GoogleTasksClient`, `RemoteTask`, `RemoteTaskList`).
- Se agrega la abstracción `provider` (`TaskProvider`, `ProviderException`) con la
  superficie completa de la Google Tasks API: listas (list/get/insert/update/delete)
  y tareas (list/get/insert/update/delete/move/clear).
- Se implementa `GoogleTasksProvider` (reemplaza `HttpGoogleTasksClient`) y se adapta
  `GoogleAuth` a `ProviderException`.
- El modelo adopta el esquema de Google Tasks: `id` de texto (sin id local numérico),
  `TaskList` expone `title`, y `Task` incorpora `due` (fecha de vencimiento).
- `TaskService` pasa a depender de `TaskProvider` y mantiene una caché en memoria de
  la sesión; cada mutación se aplica en vivo contra el proveedor.
- La autenticación es obligatoria al inicio (sin modo offline).
- La UI soporta la fecha de vencimiento: campo opcional al crear/editar tarea, acción
  "Fecha" en el menú de acciones y visualización junto al título.
- Se eliminan los atajos de sincronización (`s`) y login (`g`); el zoom pasa a ser
  de solo sesión.
- Tests: se agrega `FakeTaskProvider`; se reescriben los tests de servicio y se
  actualizan los de la capa `cli`; se eliminan los tests de `repository` y `sync`.

## Impact
- Specs afectadas: se crea `task-provider`; se marcan como removidas
  `google-tasks-sync` y `json-persistence`; se actualizan `task-management`,
  `task-lists`, `ui-zoom`, `interactive-cli`, `task-action-menu` y `output-formatting`.
- Sin persistencia local: los datos viven únicamente en Google Tasks; se requiere
  conexión y autenticación.
- La descripción (`notes`) queda fuera de alcance por ahora.
