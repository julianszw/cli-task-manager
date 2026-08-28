# Project Context

## Purpose
CLI Task Tracker: aplicación de terminal en Java para crear, listar y completar
tareas, agrupadas en listas. Proyecto de práctica orientado a reforzar arquitectura
en capas, operando exclusivamente contra la Google Tasks API como única fuente de
datos (definido en `provider`).

## Tech Stack
- Java 21 (sin frameworks externos)
- Maven (build y tests)
- JUnit para tests (`src/test/java/tasktracker`)
- Lanterna (`com.googlecode.lanterna:lanterna`) para la TUI: ventanas, paneles,
  componentes (lista con render custom, caja de texto) y manejo de teclado.
- Google Tasks API (`google-api-services-tasks`, `google-api-client`,
  `google-oauth-client-jetty`) como única fuente de datos, con OAuth de aplicación
  de escritorio.
- Lenguaje visual definido en `visual-style`: tema oscuro único, bordes redondeados,
  lista con íconos de estado y barra de estado inferior fija.
- Logo ASCII "Task Manager" en la cabecera (`app-logo`), generado con caracteres de
  bloque (sin recursos gráficos externos) y adaptado al ancho del terminal.

## Architecture
Estructura en capas, sin dependencias circulares:

- `model` — entidades del dominio (`Task`, `TaskList`, `TaskStatus`). Sin lógica de negocio, solo estado y comportamiento propio de la entidad.
- `provider` — abstracción del backend de tareas (`TaskProvider`): define las operaciones sobre listas y tareas, de modo que la fuente de datos sea intercambiable (Google Tasks ahora, otros proveedores después).
- `service` — lógica de negocio y orquestación. Valida reglas, coordina `provider`, expone operaciones a `cli`.
- `google` — adaptador de infraestructura: autenticación OAuth (`GoogleAuth`) y cliente de Google Tasks (`GoogleTasksProvider`, implementa la abstracción de `provider`).
- `cli` — interacción con el usuario por terminal. Ventanas, atajos y formato de output. Sin lógica de negocio ni acceso a datos directo.
- `exception` — excepciones propias del dominio (ej: `TaskNotFoundException`).

Regla de dependencia: `cli` → `service` → `provider` → `model`, con `google`
implementando la interfaz de `provider`. Nunca al revés.

## Conventions
- La interacción es una vista única de tareas (TUI) con atajos de teclado; no hay
  prompt de texto ni comandos por nombre.
- Las tareas se agrupan en listas (`task-lists`): la vista abre sobre la lista
  activa, se navega con `Tab`/`Shift+Tab` y se crean listas con `n`.
- La presentación sigue `visual-style` (tema oscuro único, sin alternancia de tema).
- Excepciones de dominio son unchecked y se muestran como mensajes legibles al usuario.
- Tests unitarios por capa (`service`, `cli`): la capa `cli` se testea
  con foco en la vista/entrada, no en lógica de negocio.

## Out of scope (por ahora)
- Persistencia local y modo offline (Google Tasks es la única fuente de datos).
- Otros proveedores además de Google Tasks (por ejemplo, Microsoft TO DO).
- Interfaz gráfica o web.
- Multi-usuario.