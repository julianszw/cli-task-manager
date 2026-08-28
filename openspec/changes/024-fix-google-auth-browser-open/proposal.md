# 024 — Fix Google Auth Browser Open

## Why

El fix 023 resolvió correctamente la suspensión de la pantalla de Lanterna
antes de autenticar, pero el navegador nunca se abría. La causa raíz es que
`AuthorizationCodeInstalledApp` usa internamente `java.awt.Desktop.browse()`
para abrir la URL de OAuth. En Linux, `Desktop.browse()` falla silenciosamente
(especialmente tras un `screen.stopScreen()` de Lanterna), y la excepción
resultante era atrapada por el `catch (Exception e)` genérico de
`GoogleAuth.authorize()`, relanzándola como `SyncException` sin haber abierto
el browser.

## What Changes

- `GoogleAuth.authorize()`: reemplaza el constructor por defecto de
  `AuthorizationCodeInstalledApp` por uno que recibe un `Browser` personalizado.
  El nuevo browser:
  1. Imprime la URL de autorización en stdout (visible porque la pantalla de
     Lanterna ya fue suspendida).
  2. Intenta abrir la URL con `xdg-open` (el estándar de escritorio en Linux).
  3. Si `xdg-open` falla, el usuario puede copiar la URL impresa manualmente.

## Impact

- **Fix**: El navegador se abre correctamente al presionar `g` o `s`.
- **Fallback**: Si `xdg-open` no está disponible, la URL se muestra en
  la terminal para que el usuario la copie manualmente.
- **Sin regresiones**: No se modifican constructores ni interfaces públicas.
