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

La app abre un prompt (`> `) donde se ingresan comandos.

## Comandos

| Comando | Descripción |
|---------|-------------|
| `add <título>` | Crea una tarea nueva |
| `list` | Abre el modo de navegación interactiva |
| `complete <id>` | Marca una tarea como completada |
| `purge` | Elimina todas las tareas completadas |
| `help` | Muestra los comandos disponibles |

## Modo interactivo (`list`)

Muestra las tareas en una tabla (completadas tachadas y con color). Navegación:

| Tecla | Acción |
|-------|--------|
| `↑` / `k` | Subir selección |
| `↓` / `j` | Bajar selección |
| `c` | Completar la tarea seleccionada |
| `d` | Eliminar la tarea seleccionada |
| `p` | Purgar tareas completadas |
| `q` / `Esc` | Salir |

## Tests

```bash
mvn test
```
