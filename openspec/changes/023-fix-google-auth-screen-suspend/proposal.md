# 023 — Fix Google Auth Screen Suspend

## Why

Al presionar `g` (login Google) o `s` (sync), el flujo OAuth de Google
intentaba abrir el browser mientras Lanterna tenía la terminal capturada en
modo raw/alternativo. Esto impedía que `Desktop.browse()` funcionara
correctamente y cualquier excepción era atrapada genéricamente, mostrando
solo "No se pudo autenticar con Google" sin abrir el browser.

## What Changes

- `App.java`: pasa la instancia de `Screen` a `LanternaTaskTrackerView`.
- `LanternaTaskTrackerView`: recibe `Screen` y la propaga a `TaskListWindow`.
- `TaskListWindow`:
  - Nuevo campo `screen` (puede ser `null` para compatibilidad con tests).
  - Nuevo método `authenticateWithScreenSuspended()` que:
    1. Llama a `screen.stopScreen()` para restaurar la terminal normal.
    2. Ejecuta `syncService.ensureAuthenticated()` (que abre el browser).
    3. Restaura la TUI con `screen.startScreen()` en un bloque `finally`.
  - `loginGoogle()` y `syncWithGoogle()` delegan la autenticación a este
    nuevo método cuando el usuario no está autenticado.

## Impact

- **Fix**: El browser se abre correctamente al presionar `g` o `s` sin
  credenciales almacenadas.
- **Sin regresiones**: Los constructores de 1 y 2 argumentos de
  `TaskListWindow` pasan `null` como screen, manteniendo compatibilidad
  con tests existentes.
