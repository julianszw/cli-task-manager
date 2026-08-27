# Eliminar archivos y código no usados

## Why

Tras implementar el lenguaje visual (`014-visual-style`), el repositorio acumuló
ruido: artefactos de build e IDE sin ignorar (`target/`, `dependency-reduced-pom.xml`,
`.idea/`, `*.iml`) y restos de código que quedaron sin uso (`VisualStyle.ACCENT_ALT`,
`TaskListWindow.setError`, `MessageKind.ERROR` y la rama de error del banner). Mantenerlos
agrega complejidad innecesaria y ensucia `git status` (Clean Code: eliminar dead code
y artefactos generados).

## What Changes

- `.gitignore`: añade `target/`, `.idea/`, `*.iml` y `dependency-reduced-pom.xml`.
- Se quitan del control de versiones `.idea/misc.xml` y `cli-task-tracker.iml`
  (ya ignorados por la regla anterior); se borran del disco `target/` y
  `dependency-reduced-pom.xml` (regenerables).
- `VisualStyle`: se elimina `ACCENT_ALT` (magenta) sin uso.
- `TaskListWindow`: se elimina `setError` sin uso.
- `MessageKind`: se elimina el valor `ERROR` (solo se usan `INFO` y `WARN`).
- `TaskViewRenderer`: se elimina la rama `ERROR` del banner y la constante `ERROR_GLYPH`.
- Spec `visual-style`: se quita "magenta" de la paleta y el escenario de error en el
  banner pasa a describir un aviso (amarillo); los errores (título vacío) se muestran
  con `⚠` rojo en su propio contexto (`AddTaskWindow`).

## Impact

- No cambia el comportamiento funcional de la vista ni la persistencia.
- `git status` deja de mostrar artefactos de build/IDE.
- Verificación: `mvn test` y `mvn package`.
