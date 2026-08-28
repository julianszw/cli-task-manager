# Tareas

- [x] Modelo con ids de texto y campo `due` (`Task`, `TaskList`, excepciones)
- [x] Abstracción `provider` (`TaskProvider`, `ProviderException`)
- [x] `GoogleTasksProvider` con toda la superficie de la API (listas y tareas)
- [x] Adaptar `GoogleAuth` a `ProviderException`
- [x] Reescribir `TaskService` sobre `TaskProvider` (caché de sesión)
- [x] Eliminar capa `repository` y `sync` (producción y tests)
- [x] Actualizar `App`, `LanternaTaskTrackerView` y `TaskListWindow`
- [x] UI de fecha: `AddTaskWindow`, `EditTaskWindow`, `TaskDateWindow`, renderer y menú
- [x] Quitar atajos `s`/`g`; zoom de solo sesión
- [x] Tests: `FakeTaskProvider`, reescribir/actualizar tests de service y cli
- [x] `mvn clean test` en verde (119 tests)
