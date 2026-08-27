# Project Context

## Purpose
CLI Task Tracker: aplicación de terminal en Java para crear, listar y completar
tareas, agrupadas en listas. Proyecto de práctica orientado a reforzar arquitectura
en capas y manejo de estado con persistencia en un archivo JSON local (definido en
`repository`).

## Tech Stack
- Java 21 (sin frameworks externos)
- Maven (build y tests)
- JUnit para tests (`src/test/java/tasktracker`)
- Lanterna (`com.googlecode.lanterna:lanterna`) para la TUI: ventanas, paneles,
  componentes (lista con render custom, caja de texto) y manejo de teclado.
- Lenguaje visual definido en `visual-style`: tema oscuro único, bordes redondeados,
  lista con íconos de estado y barra de estado inferior fija.
- Logo ASCII "Task Manager" en la cabecera (`app-logo`), generado con caracteres de
  bloque (sin recursos gráficos externos) y adaptado al ancho del terminal.

## Architecture
Estructura en capas, sin dependencias circulares:

- `model` — entidades del dominio (`Task`, `TaskList`, `TaskStatus`). Sin lógica de negocio, solo estado y comportamiento propio de la entidad.
- `repository` — acceso y persistencia de datos (en memoria o archivo JSON). Abstrae dónde y cómo se guardan las tareas.
- `service` — lógica de negocio y orquestación. Valida reglas, coordina `repository`, expone operaciones a `cli`.
- `cli` — interacción con el usuario por terminal. Comandos, parsing de input, formato de output. Sin lógica de negocio ni acceso a datos directo.
- `exception` — excepciones propias del dominio (ej: `TaskNotFoundException`).

Regla de dependencia: `cli` → `service` → `repository` → `model`. Nunca al revés.

## Conventions
- La interacción es una vista única de tareas (TUI) con atajos de teclado; no hay
  prompt de texto ni comandos por nombre.
- Las tareas se agrupan en listas (`task-lists`): la vista abre sobre la lista
  activa, se navega con `Tab`/`Shift+Tab` y se crean listas con `n`.
- La presentación sigue `visual-style` (tema oscuro único, sin alternancia de tema).
- Excepciones de dominio son unchecked y se muestran como mensajes legibles al usuario.
- Tests unitarios por capa (`repository`, `service`, `cli`): la capa `cli` se testea
  con foco en la vista/entrada, no en lógica de negocio.

## Out of scope (por ahora)
- Persistencia en base de datos (usa un archivo JSON local).
- Interfaz gráfica o web.
- Multi-usuario.