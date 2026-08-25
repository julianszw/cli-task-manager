# Tasks

- [x] Crear `JsonParseException` (paquete `repository`)
- [x] Crear `JsonTasksCodec` (encode/decode de array JSON de tareas)
- [x] Agregar `persist()` a `TaskRepository` y no-op en `InMemoryTaskRepository`
- [x] Crear `JsonTaskRepository` (carga inicial, ids sin colisión, persistencia por operación, tolerancia a fallos)
- [x] Actualizar `TaskService.completeTask` para persistir
- [x] Conectar `App` a `JsonTaskRepository` y mostrar avisos en la vista
- [x] Agregar tests de `JsonTasksCodec`, `JsonTaskRepository` y persistencia en `TaskService`
- [x] Actualizar `project.md` y `.gitignore`
- [x] Compilar y ejecutar tests (`mvn test`, `mvn package`)
