# Tasks

- [x] Crear `TaskList` (id, name) y añadir `listId` a `Task`
- [x] Crear `TaskListNotFoundException`
- [x] Ampliar `TaskRepository` (findAllLists, saveList, findListById, removeCompleted(listId))
- [x] Implementar listas en `InMemoryTaskRepository` y `JsonTaskRepository`
- [x] Renombrar `JsonTasksCodec` → `JsonStoreCodec` (formato lists+tasks) con migración del array plano a "Inbox"
- [x] Actualizar `TaskService` (listLists, createList, addTask(listId), listTasks(listId), purgeCompletedTasks(listId))
- [x] `App`: crear lista inicial "Inbox" si no existe ninguna
- [x] `AddTaskWindow` con id de lista; crear `NewListWindow`
- [x] `TaskListWindow`: lista activa, Tab/Shift+Tab, tecla n, crear/purgar por lista
- [x] `TaskViewRenderer`: indicador de lista en cabecera y atajos `Tab`/`n`
- [x] Tests (service, repository, codec, ventanas, list window)
- [x] Actualizar specs (`json-persistence`, `task-management`, `interactive-cli`) y docs (`README.md`, `project.md`)
- [x] Compilar y ejecutar tests (`mvn test`, `mvn package`)
