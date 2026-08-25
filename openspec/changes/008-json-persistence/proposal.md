# Persistencia de tareas en archivo JSON

## Why

La aplicación guardaba las tareas únicamente en memoria (`InMemoryTaskRepository`),
por lo que se perdían al cerrar la terminal. La spec `json-persistence` exige
persistirlas en un archivo JSON del directorio de trabajo y recargarlas al arrancar.

## What Changes

- Repositorio: nuevo `JsonTaskRepository` que carga tareas desde un archivo JSON al
  iniciar y sobrescribe el archivo tras cada operación que modifica tareas
  (creación, completado, eliminación y purga).
- Códec: `JsonTasksCodec` (serialización/deserialización JSON sin dependencias
  externas) y `JsonParseException` para entradas inválidas.
- Interfaz `TaskRepository`: nuevo método `persist()`; `InMemoryTaskRepository`
  lo implementa como no-op.
- Servicio: `TaskService.completeTask` invoca `persist()` tras marcar la tarea.
- App: usa `JsonTaskRepository` sobre `tasks.json` y muestra avisos de carga/escritura
  fallida a través de la vista.
- Se tolera un fallo de escritura sin perder las tareas en memoria (aviso al usuario).

## Impact

- Los datos persisten entre ejecuciones en `tasks.json` (directorio de trabajo).
- Archivos inexistentes arrancan vacíos; archivos corruptos arrancan vacíos con aviso.
- Sin dependencias nuevas (JSON se serializa a mano).
- Verificación: `mvn test` y `mvn package`.
