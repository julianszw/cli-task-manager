# Eliminar código no usado (capa de comandos CLI de texto)

## Why

La aplicación evolucionó de una CLI con prompt de texto a una TUI de vista única
(`interactive-cli`). La capa de comandos (`Command`, `CommandDispatcher`,
`CommandRegistry`, los comandos `add`/`complete`/`exit`/`help`/`list`/`purge` y la
interfaz `TaskTrackerView`) quedó inalcanzable: `App` solo lanza la TUI de Lanterna
y nada instancia el dispatcher. Es dead code que agrega complejidad innecesaria
(Clean Code: eliminar smells como *needless complexity* y repetición).

## What Changes

- Se elimina la capa de comandos CLI de texto y sus tests (10 clases de producción
  y 8 de test, incluida `FakeTaskTrackerView`).
- Se elimina la interfaz `TaskTrackerView` (solo la implementaba el fake de test).
- `TaskNotFoundException` pierde el campo `id` y el getter `getId()` (sin usos).
- `JsonTaskRepository` pierde el constructor de un argumento (sin usos).
- La spec `cli-interface` queda marcada como removida (igual que `main-menu`).
- `project.md` y `README.md` dejan de referirse al prompt y a los comandos por nombre.

## Impact

- La aplicación queda con una única forma de interacción: la vista única de tareas (TUI).
- No cambia el comportamiento de la vista única ni la persistencia.
- Verificación: `mvn test` y `mvn package`.
