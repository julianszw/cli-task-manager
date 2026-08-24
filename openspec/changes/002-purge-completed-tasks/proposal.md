# Agregar comando purge para eliminar tareas completadas

## Why

La aplicación permitía crear, listar y completar tareas, pero no había forma
de limpiar las tareas ya completadas. Se agrega el comando `purge` para
eliminar todas las tareas en estado `COMPLETED`, dejando intactas las
pendientes.

## What Changes

- Modelo: `Task.isCompleted()` (helper de estado).
- Repositorio: `TaskRepository.removeCompleted()` e implementación en
  `InMemoryTaskRepository` (elimina y devuelve las completadas).
- Servicio: `TaskService.purgeCompletedTasks()`.
- CLI: nuevo `PurgeCompletedCommand` (nombre `purge`), registrado en `App`.
  Lista las tareas eliminadas, o muestra un mensaje informativo si no hay.
- Specs: nuevas requirements en `task-management` y `cli-interface`.
- Tests: repository, service y cli para el nuevo comportamiento.

## Impact

- `App` registra el comando `purge` (visible también vía `help`).
- `TaskRepository` gana un método, por lo que cualquier implementación
  futura del repositorio debe soportarlo.
- Verificación: `mvn test` en verde.
