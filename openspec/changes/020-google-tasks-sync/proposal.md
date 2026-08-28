# Sincronización bidireccional con Google Tasks

## Why

La aplicación persiste las tareas solo en un archivo JSON local (`json-persistence`).
El usuario quiere que esas mismas tareas (título, estado y lista) se reflejen en
Google Tasks —la API que respalda las tareas tachables visibles en Google
Calendar— y que los cambios hechos en Google se reflejen de vuelta en la app. La
spec `google-tasks-sync` define este comportamiento como una sincronización
bidireccional, sin reemplazar la persistencia local.

## What Changes

- Modelo: `Task` y `TaskList` añaden `remoteId` (id remoto de Google, `String`) y
  `updatedAt` (marca de tiempo de la última modificación, epoch millis) para
  correlacionar ambos lados y resolver conflictos por "última modificación gana".
- Persistencia (`json-persistence`): `JsonStoreCodec` serializa/deserializa los
  campos `remoteId` y `updatedAt` como campos opcionales (compatibilidad hacia
  atrás con archivos existentes). Nuevo sidecar `sync-state.json` con los ids
  remotos conocidos (para detectar eliminaciones sin resurrección) y la última
  fecha de sincronización.
- Paquete `tasktracker.google`: `GoogleAuth` (flujo OAuth de app instalada con
  navegador y refresh token persistido) y `GoogleTasksClient` (wrapper fino sobre la
  Tasks API: listar/crear/actualizar/eliminar tareas y listas).
- Paquete `tasktracker.sync`: `TaskSyncService` reconcilia local y remoto: vincula
  por `remoteId`, sube cambios locales, baja cambios remotos, resuelve conflictos
  por `updatedAt` y respeta los tombstones para no resucitar eliminados.
- `App`: construye el cliente, instancia el motor de sync y sincroniza al iniciar si
  hay un token guardado. Sin credenciales configuradas, la app arranca en modo
  local sin fallar.
- UI: atajo `g` para iniciar sesión en Google (flujo OAuth) y atajo `s` para
  sincronizar (dispara login si no hay sesión), con mensaje de resultado en el
  banner. Sincronización síncrona en esta versión.
- Dependencias Maven: `google-api-services-tasks`, `google-api-client`
  (`google-api-client-gson`), `google-oauth-client-jetty` y
  `google-auth-library-oauth2-http`.

## Impact

- Nueva capacidad `google-tasks-sync` (spec en `openspec/specs/`).
- Se actualiza `json-persistence` (campos opcionales `remoteId`/`updatedAt` y
  sidecar de sync).
- La persistencia local sigue siendo la fuente de datos; Google Tasks es un mecanismo
  adicional de sincronización, no un reemplazo.
- Verificación: `mvn test` y `mvn package`.
