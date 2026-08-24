# Formato de salida "pretty" (tabla, tachado y color)

## Why

La salida de `list` y `purge` era un volcado plano con `printf`, sin
estructura ni distinción visual. Se busca una salida más legible: tabla con
columnas alineadas, tareas completadas tachadas y color por estado.

## What Changes

- CLI: nuevo `TaskTableFormatter` que renderiza una `List<Task>` como tabla
  alineada (columnas `ID`, `ESTADO`, `TÍTULO`) con códigos ANSI: color por
  estado (`PENDING` amarillo, `COMPLETED` verde), tachado de la fila completa
  para completadas y resaltado opcional de la fila seleccionada.
- `PurgeCompletedCommand` usa el formatter para listar las tareas eliminadas.
- Specs: nueva capacidad `output-formatting`.
- Tests: `TaskTableFormatterTest`.

## Impact

- La salida de `purge` ahora incluye códigos ANSI (color/tachado).
- El formatter queda disponible para el modo interactivo (`interactive-cli`).
- Verificación: `mvn test` en verde.
