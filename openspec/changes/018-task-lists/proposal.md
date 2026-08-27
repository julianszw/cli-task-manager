# Agrupación de tareas en listas con navegación Tab

## Why

La vista única (`interactive-cli`) gestionaba una única lista plana de tareas. El
spec `task-lists` (definido pero sin implementar) exige agrupar las tareas en
listas, navegar entre ellas con `Tab`/`Shift+Tab`, mostrar un indicador de la lista
activa ("nombre · 1/3") y crear listas nuevas con la tecla `n`. La persistencia
(`json-persistence`) también debía evolucionar para guardar las listas, y el purge
pasaba a operar solo sobre la lista activa (decisión confirmada).

## What Changes

- `TaskList` (nuevo modelo): `id` y `name`. `Task` añade `listId` para asociar cada
  tarea a exactamente una lista.
- `TaskRepository` (y sus implementaciones `InMemoryTaskRepository` y
  `JsonTaskRepository`): operaciones de listas (`findAllLists`, `saveList`,
  `findListById`) y `removeCompleted(long listId)` acotado a una lista.
- `JsonTasksCodec` → `JsonStoreCodec`: el archivo pasa de un array plano de tareas a
  un objeto `{"lists":[...],"tasks":[...]}` con `listId` por tarea. El formato
  anterior (array plano) se migra automáticamente a una lista "Inbox" sin perder
  tareas.
- `TaskService`: `listLists()`, `createList(name)` (valida nombre no vacío),
  `addTask(listId, title)` (valida lista y título), `listTasks(listId)` y
  `purgeCompletedTasks(listId)`. Las operaciones por id (`completeTask`,
  `reopenTask`, `renameTask`, `deleteTask`) no cambian.
- `App`: crea la lista inicial "Inbox" al arrancar si no existe ninguna.
- UI: `AddTaskWindow` recibe el id de la lista activa; nuevo `NewListWindow`
  (espejo de `AddTaskWindow` para nombres de lista); `TaskListWindow` mantiene la
  lista activa y navega con `Tab`/`Shift+Tab`, abre la creación de listas con `n`,
  crea tareas en la lista activa y purga solo esa lista; el renderer muestra el
  indicador de lista en la cabecera y añade los atajos `Tab` y `n` a la barra.

## Impact

- Nueva capacidad `task-lists` (spec ya definida en `openspec/specs/`).
- Se actualizan `json-persistence` (formato + migración), `task-management` (crear
  tarea en lista, purge por lista) e `interactive-cli` (atajos `Tab`/`n`, crear en
  la lista activa, purge de la lista activa).
- El purge deja de ser global: opera solo sobre la lista activa.
- Verificación: `mvn test` y `mvn package`.
