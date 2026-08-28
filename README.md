# CLI Task Tracker

Aplicación de terminal en Java para crear, listar, completar y eliminar tareas.

## Requisitos

- Java 21
- Maven 3.8+

## Compilar

```bash
mvn package
```

Genera un jar autocontenido (incluye dependencias) en `target/cli-task-tracker-1.0.0.jar`.

## Ejecutar

```bash
java -jar target/cli-task-tracker-1.0.0.jar
```

La app abre directamente la vista única de tareas con un tema oscuro, un logo
"Task Manager" en la cabecera, listas de tareas navegables (cada tarea pertenece a
una lista), íconos de estado (`○` pendiente, `✓` verde completado), fecha de
vencimiento junto al título y una barra de estado inferior con título, contador y
atajos. Las tareas completadas se muestran con el texto tachado en gris y se agrupan
al final de la lista. El menú de acciones (`Enter`) tiene navegación circular y la
interfaz permite hacer zoom (`Ctrl+=`, `Ctrl+-`, `Ctrl+0`), nivel que solo dura la
sesión actual.

## Teclas

| Tecla | Acción |
|-------|--------|
| `↑` / `k` | Subir selección |
| `↓` / `j` | Bajar selección |
| `Enter` | Abrir menú de acciones (completar, reabrir, eliminar, editar, fecha, mover) |
| `Tab` / `Shift+Tab` | Cambiar a la lista siguiente / anterior |
| `a` | Crear una tarea nueva en la lista activa |
| `n` | Crear una lista nueva |
| `c` | Completar la tarea seleccionada |
| `r` | Reabrir la tarea seleccionada |
| `d` | Eliminar la tarea seleccionada |
| `p` | Purgar tareas completadas de la lista activa |
| `Ctrl+=` / `Ctrl+-` | Aumentar / disminuir el zoom de la interfaz |
| `Ctrl+0` | Restablecer el zoom al nivel por defecto |
| `q` / `Esc` | Salir (con confirmación) |

## Google Tasks como fuente de datos

La app opera exclusivamente contra la Google Tasks API (las mismas tareas tachables
que se ven en Google Calendar): no hay persistencia local. Al iniciar, se requiere
autenticación; si no hay una sesión guardada, se abre el navegador para autorizar el
acceso.

### Configuración

Se necesita una credencial OAuth de "aplicación de escritorio" en Google Cloud
Console con el ámbito de Google Tasks. La credencial se lee desde variables de
entorno o desde un archivo `credentials.json` en el directorio de trabajo:

```bash
export GOOGLE_CLIENT_ID="<tu-client-id>.apps.googleusercontent.com"
export GOOGLE_CLIENT_SECRET="<tu-client-secret>"
java -jar target/cli-task-tracker-1.0.0.jar
```

El token se guarda en el directorio `google-tokens/` (ignorado por git).

## Tests

```bash
mvn test
```
