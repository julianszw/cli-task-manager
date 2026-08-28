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
una lista), íconos de estado (`○` pendiente, `✓` verde completado) y una barra de
estado inferior con título, contador y atajos. Las tareas completadas se muestran
con el texto tachado en gris y se agrupan al final de la lista. El menú de acciones
(`Enter`) tiene navegación circular y la interfaz permite hacer zoom (`Ctrl+=`,
`Ctrl+-`, `Ctrl+0`), nivel que se recuerda entre sesiones.

## Teclas

| Tecla | Acción |
|-------|--------|
| `↑` / `k` | Subir selección |
| `↓` / `j` | Bajar selección |
| `Enter` | Abrir menú de acciones (completar, reabrir, eliminar, editar, mover) |
| `Tab` / `Shift+Tab` | Cambiar a la lista siguiente / anterior |
| `a` | Crear una tarea nueva en la lista activa |
| `n` | Crear una lista nueva |
| `c` | Completar la tarea seleccionada |
| `r` | Reabrir la tarea seleccionada |
| `d` | Eliminar la tarea seleccionada |
| `p` | Purgar tareas completadas de la lista activa |
| `g` | Iniciar sesión de Google (abre el navegador para autorizar) |
| `s` | Sincronizar con Google Tasks (abre login si no hay sesión) |
| `Ctrl+=` / `Ctrl+-` | Aumentar / disminuir el zoom de la interfaz |
| `Ctrl+0` | Restablecer el zoom al nivel por defecto |
| `q` / `Esc` | Salir (con confirmación) |

## Sincronización con Google Tasks

La app puede sincronizar de forma bidireccional las tareas y listas locales con
Google Tasks (las mismas tareas tachables que se ven en Google Calendar): título,
estado y lista. Se sincroniza al iniciar si hay una sesión guardada y manualmente
con la tecla `s`. La persistencia local (`tasks.json`) sigue siendo la fuente de
datos; Google Tasks es un mecanismo adicional.

### Configuración

Se necesita una credencial OAuth de "aplicación de escritorio" en Google Cloud
Console con el ámbito de Google Tasks. La credencial se lee desde variables de
entorno o desde un archivo `credentials.json` en el directorio de trabajo:

```bash
export GOOGLE_CLIENT_ID="<tu-client-id>.apps.googleusercontent.com"
export GOOGLE_CLIENT_SECRET="<tu-client-secret>"
java -jar target/cli-task-tracker-1.0.0.jar
```

Con la app abierta, presiona `g` para iniciar sesión: se abre el navegador para
autorizar el acceso (o `s` para autenticar y sincronizar de una vez). El token se
guarda en el directorio `google-tokens/` y el estado de sincronización en
`sync-state.json` (ambos ignorados por git). Si no hay credenciales configuradas,
la app funciona en modo local y muestra un aviso al intentar autenticarse.

## Tests

```bash
mvn test
```
