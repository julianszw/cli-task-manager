# Tasks

- [x] Añadir `remoteId` y `updatedAt` a `Task` y `TaskList`
- [x] `JsonStoreCodec`: serializar/deserializar `remoteId` y `updatedAt` opcionales
- [x] `JsonTaskRepository`: stamp `updatedAt` en mutaciones y propagar nuevos campos
- [x] Sidecar `sync-state.json` (ids remotos conocidos + última sincronización)
- [x] Dependencias Maven de Google Tasks + auth
- [x] `GoogleAuth` (OAuth navegador + refresh token persistido)
- [x] `GoogleTasksClient` (listar/crear/actualizar/eliminar tareas y listas)
- [x] `TaskSyncService` (vinculación, subir/bajar, conflictos, tombstones)
- [x] `App`: instanciar cliente y sync; sincronizar al iniciar si hay token
- [x] `TaskListWindow`: atajos `g` (login) y `s` (sincronizar) + mensaje de resultado
- [x] `TaskViewRenderer`/`ShortcutBar`: añadir atajo `s`
- [x] Tests (codec, sync con cliente fake, service)
- [x] Actualizar specs (`json-persistence`) y docs
- [x] Compilar y ejecutar tests (`mvn test`, `mvn package`)
