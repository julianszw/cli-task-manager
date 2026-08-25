# Proposal: scripts de lanzamiento (doble click)

## Why

Arrancar la aplicación hoy requiere dos comandos manuales (`mvn package` y
`java -jar ...`). Un usuario final quiere un atajo de doble click que compile y
levante la app en una terminal.

## What Changes

- Añade `run.sh`: compila solo si falta `target/cli-task-tracker-1.0.0.jar` y
  luego ejecuta el jar.
- Añade `rebuild-run.sh`: recompila siempre y luego ejecuta el jar.
- Ambos usan `cd "$(dirname "$0")"` para funcionar desde cualquier directorio,
  `set -e` para abortar ante errores, y `mvn package -q -DskipTests` para un
  arranque ágil.

## Impact

- Sin cambios en el código Java ni en las capacidades existentes.
- Requiere permisos de ejecución (`chmod +x`).
- El doble click depende de la preferencia "Ejecutar en terminal" del gestor de
  archivos.
