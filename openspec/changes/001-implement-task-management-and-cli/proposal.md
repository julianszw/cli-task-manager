# Implementar task-management y cli-interface

## Why

El proyecto tenía solo el esqueleto de directorios y archivos de modelo
rotos (paquetes incorrectos, campos sin definir). Era necesario implementar
las specs `task-management` y `cli-interface` para que la aplicación funcione
de punta a punta.

## What Changes

- Modelo: `Task` (id numérico, título, estado) y `TaskStatus`
  (`PENDING`/`COMPLETED`).
- Excepción de dominio: `TaskNotFoundException`.
- Repositorio: interfaz `TaskRepository` + `InMemoryTaskRepository`
  (ids auto-incrementales).
- Servicio: `TaskService` (`addTask`, `listTasks`, `completeTask`).
- CLI: interfaz `Command`, `AddTaskCommand`, `ListTasksCommand`,
  `CompleteTaskCommand`, `CommandRegistry` y `App` (REPL).
- Build: `pom.xml` (Java 21 + JUnit 5.11.4).
- Tests: unitarios de repository, service y cli.

## Impact

- Paquetes rellenados: `repository`, `service`, `exception`, `cli`.
- Se corrige el package de `model` (`tasktracker.model`).
- Verificación: 22 tests en verde y smoke test manual del REPL.
