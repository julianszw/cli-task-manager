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
con el texto tachado en gris y se agrupan al final de la lista.

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
| `q` / `Esc` | Salir (con confirmación) |

## Tests

```bash
mvn test
```
