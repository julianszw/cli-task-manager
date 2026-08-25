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

La app abre directamente la vista única de tareas con un tema oscuro, una lista con
íconos de estado (`○` pendiente, `✓` verde completado) y una barra de estado inferior
con título, contador y atajos.

## Teclas

| Tecla | Acción |
|-------|--------|
| `↑` / `k` | Subir selección |
| `↓` / `j` | Bajar selección |
| `a` | Crear una tarea nueva |
| `c` | Completar la tarea seleccionada |
| `r` | Reabrir la tarea seleccionada |
| `d` | Eliminar la tarea seleccionada |
| `p` | Purgar tareas completadas |
| `q` / `Esc` | Salir |

## Tests

```bash
mvn test
```
