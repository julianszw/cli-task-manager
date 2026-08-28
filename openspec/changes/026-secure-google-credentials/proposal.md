# 026 — Secure Google Credentials

## Why

Las credenciales de Google Auth (`GOOGLE_CLIENT_ID` y `GOOGLE_CLIENT_SECRET`) se
pasaban mediante variables de entorno y si no estaban, el código intentaba leer
el archivo `credentials.json`. Mantener `credentials.json` en el workspace 
generaba un riesgo de seguridad de comitear los secretos. Además, el usuario tenía 
que exportar las variables a mano en cada sesión.

## What Changes

- Se eliminó cualquier archivo `credentials.json` existente.
- Se agregó `.env` a `.gitignore` para prevenir subirlo al control de versiones.
- Se modificaron los scripts de arranque (`run.sh` y `rebuild-run.sh`) para
  ejecutar automáticamente `source .env` si el archivo existe.
- `GoogleAuth.java`: Se agregó `.trim()` al leer las variables de entorno para 
  evitar errores de parseo (e.g. `invalid_client` por un salto de línea `%0A`).

## Impact

- **Seguridad**: Ya no hay riesgo de commitear credenciales (verificado con `git ls-files`).
- **Usabilidad**: No es necesario hacer `export` en cada terminal, los scripts 
  de lanzamiento inicializan el entorno de manera aislada.
