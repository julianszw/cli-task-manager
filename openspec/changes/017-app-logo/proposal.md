# Logo ASCII "Task Manager" como cabecera

## Why

La vista única (`interactive-cli`) arrancaba mostrando directamente la lista de
tareas, sin identidad visual en la cabecera. El spec `app-logo` (definido pero sin
implementar) exige un logo "Task Manager" dibujado en letras grandes con caracteres
de bloque, situado arriba de todo, en el color de acento de `visual-style`, que se
adapte al ancho del terminal.

## What Changes

- Nuevo `AppLogo`: clase pura del paquete `cli` que genera el texto "TASK MANAGER"
  en letras de bloque (5 filas de ancho uniforme, sin recursos externos) y expone
  `lines()` (arte completo), `minWidth()` y `fit(width)` (omite el logo si el ancho
  es insuficiente; si no, trunca cada línea al ancho disponible).
- `TaskViewRenderer`: dibuja el logo centrado en la parte superior, en color de
  acento (cian), reservando una fila de separación entre el logo y la caja de la
  lista. La lista y la barra de estado se desplazan hacia abajo para dejar hueco a
  la cabecera. Si el terminal es demasiado angosto, el logo se omite sin romper el
  resto de la vista.

## Impact

- Nueva capacidad `app-logo` (spec ya definida en `openspec/specs/`).
- La vista única muestra la cabecera "Task Manager" por encima de la lista.
- No cambia la persistencia ni el ruteo de teclas existente.
- Verificación: `mvn test` y `mvn package`.
