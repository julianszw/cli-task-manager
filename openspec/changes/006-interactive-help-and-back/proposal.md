# Ayuda permanente y tecla de "atrás" en el modo interactivo

## Why

El modo interactivo de `list` no comunicaba al usuario qué teclas estaban
disponibles ni ofrecía una forma explícita de volver atrás: la salida se
resolvía con `q`/`Esc`, y la navegación no dejaba claro cómo retroceder.
Se busca una interfaz más navegable y autocontenida.

## What Changes

- `Key`: nueva acción `BACK`.
- `KeyMapper`: mapea `b`/`B` a `Key.BACK`.
- `InteractiveTaskBrowser`:
  - Maneja `BACK` como salida (mismo comportamiento que `EXIT`).
  - Muestra una línea de ayuda permanente (`HELP`) con todas las teclas, tanto
    en la vista de tabla como en el caso de lista vacía.
  - La limpieza de pantalla al cambiar de selección ya existía (`clearScreen`
    en cada render) y se mantiene.
- Specs: `interactive-cli` ya refleja los requirements "Limpieza de pantalla al
  cambiar la selección", "Ayuda permanente de teclas" y "Volver atrás".
- Tests: `KeyMapperTest` (mapeo de `b`) e `InteractiveTaskBrowserTest`
  (ayuda visible y salida con `b`).

## Impact

- `Esc` y `q` siguen funcionando para salir; `b` es una alternativa explícita
  de "atrás" (mismo destino: prompt de comandos).
- La ayuda aparece siempre al pie de la vista interactiva, sin tecla de toggle.
- Verificación: `mvn test` en verde.
