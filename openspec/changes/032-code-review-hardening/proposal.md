# 032 — Code review hardening

## Why
Una revisión de código (2026-08-29, "mejora de código, performance, buenas
prácticas, dead code y documentación") detectó 17 hallazgos sin criticidad de datos
ni seguridad, pero con dos problemas altos de eficiencia de arranque (cliente y
transporte de Google reconstruidos en cada llamada), un bug de corrección (paginación
truncada en 100) y varias deudas de higiene de código y documentación incoherente.

## What Changes
- **Performance**: cachear `Tasks`/transporte en `GoogleTasksProvider`; cachear
  flow/transporte en `GoogleAuth` y dejar de silenciar errores de sesión.
- **Corrección**: paginar listas y tareas de la Google Tasks API hasta agotar el
  `pageToken` (antes se truncaba en 100).
- **Dead code**: recortar `TaskProvider` a las operaciones usadas (quitar
  `getTaskList`, `updateTaskList`, `deleteTaskList`, `getTask`, `clearTasks`) y sus
  implementaciones; quitar `APPLICATION_NAME` sin uso en `GoogleAuth`.
- **Modelo de dominio**: modelar la fecha de vencimiento como `LocalDate` (sin
  `String` con sufijo RFC 3339) y aislar la conversión en `GoogleTasksProvider`.
- **Clean code**: dividir `TaskListWindow.handleInput`; extraer `MenuNavigation.cycle`
  para eliminar la duplicación entre `TaskActionMenuWindow` y `OptionMenuWindow`;
  centralizar `try/catch` con un helper `call(...)` en `GoogleTasksProvider`.
- **Arranque**: separar composition root en `App.run()` y mostrar errores legibles
  sin stack trace; fijar `maven-compiler-plugin` (release 21) y añadir enforcer.
- **Documentación**: unificar el nombre de producto a "TaskMaster" (`README.md`,
  `project.md`); añadir teclas `h` y `d` al README; ajustar "sin frameworks externos"
  en `project.md`; corregir resumen de zoom en `REGISTER.md`.

## Impact
- Specs: ya alineadas en `task-provider`, `task-management` e `interactive-cli`
  (cacheo, paginación, fecha tipada, manejo de errores de arranque).
- Código: `google` (`GoogleAuth`, `GoogleTasksProvider`), `provider`
  (`TaskProvider`), `service` (`TaskService`), `model` (`Task`), `cli`
  (`TaskListWindow`, `TaskViewRenderer`, `EditTaskWindow`, `TaskActionMenuWindow`,
  `OptionMenuWindow`, `MenuNavigation`), `App`, `pom.xml`.
- Tests: `TaskServiceTest`, `AddTaskWindowTest`, `EditTaskWindowTest`,
  `FakeTaskProvider`.
