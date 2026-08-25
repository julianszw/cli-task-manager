# Project Context

## Purpose
CLI Task Tracker: aplicación de terminal en Java para crear, listar y completar tareas.
Proyecto de práctica orientado a reforzar arquitectura en capas y manejo de estado en memoria (o persistencia simple, según se defina en `repository`).

## Tech Stack
- Java 21 (sin frameworks externos)
- Maven (build y tests)
- JUnit para tests (`src/test/java/tasktracker`)
- Lanterna (`com.googlecode.lanterna:lanterna`) para la TUI: ventanas, paneles,
  componentes (tabla, caja de texto) y manejo de teclado.

## Architecture
Estructura en capas, sin dependencias circulares:

- `model` — entidades del dominio (`Task`, `TaskStatus`). Sin lógica de negocio, solo estado y comportamiento propio de la entidad.
- `repository` — acceso y persistencia de datos. Abstrae dónde y cómo se guardan las tareas.
- `service` — lógica de negocio y orquestación. Valida reglas, coordina `repository`, expone operaciones a `cli`.
- `cli` — interacción con el usuario por terminal. Comandos, parsing de input, formato de output. Sin lógica de negocio ni acceso a datos directo.
- `exception` — excepciones propias del dominio (ej: `TaskNotFoundException`).

Regla de dependencia: `cli` → `service` → `repository` → `model`. Nunca al revés.

## Conventions
- Nombres de comandos en minúscula, un verbo (`add`, `list`, `complete`).
- Cada comando implementa la interfaz `Command` (`cli/Command.java`).
- Excepciones de dominio son unchecked, se capturan en la capa `cli` para mostrar mensajes legibles al usuario.
- Tests unitarios por capa (`repository`, `service`) — la capa `cli` se testea con foco en parsing/dispatch, no en lógica de negocio.

## Out of scope (por ahora)
- Persistencia en base de datos (arranca en memoria).
- Interfaz gráfica o web.
- Multi-usuario.