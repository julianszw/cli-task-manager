# Lenguaje visual agentty (tema oscuro único + lista con íconos + barra de estado)

## Why

La vista única de tareas (`interactive-cli`) aún usaba el aspecto por defecto de
Lanterna: una `Table` con columnas `ID`/`ESTADO`/`TÍTULO`, bordes rectos, tareas
completadas tachadas y un tema claro/oscuro alternable con la tecla `t`. Las specs
`visual-style` (nueva) y `output-formatting` (reescrita) definen un lenguaje visual
distinto, inspirado en agentty: un único tema oscuro, bordes redondeados,
separadores tenues, una barra de estado inferior de altura fija y la presentación
de tareas como lista con íconos de estado. `dark-theme` queda eliminada (sin tecla
`t`) y el formato de tabla/tachado queda reemplazado.

## What Changes

- Nuevo `VisualStyle`: paleta fija (fondo oscuro, texto blanco brillante, acentos
  cian/magenta, verde=completado, amarillo=aviso, rojo=error) y un único `Theme`
  oscuro aplicado a toda la GUI. Se elimina `ThemeManager` y la tecla `t`.
- Nuevo `TaskViewComponent`: render custom que reemplaza a la `Table`. Dibuja un
  panel con borde redondeado (`╭` `─` `╮` `│` `╰` `╯`), filas con ícono de estado
  (`○` pendiente, `✓` verde completado), filas completadas atenuadas sin tachar,
  selección resaltada con `▎` + negrita y separadores tenues (`───`).
- Nueva barra de estado inferior de altura fija: franjas de acento (`▔▔▔` arriba,
  `▁▁▁` abajo), chip de título (`▎` + negrita, truncado al centro), contador de
  pendientes/completadas, línea de banner (errores con `⚠` en rojo, avisos en
  amarillo) y fila de atajos adaptativa (tecla en negrita, etiqueta atenuada; ante
  falta de ancho se omiten primero etiquetas y luego atajos, conservando el último).
- `TaskListWindow` mantiene el ruteo de teclas y las operaciones sobre `TaskService`,
  pero gestiona la selección por sí mismo y quita `t`. Los atajos ahora incluyen las
  flechas `↑`/`↓` a nivel de ventana.
- `AddTaskWindow` usa bordes redondeados y muestra el error de título vacío con `⚠`
  en rojo.
- Se elimina `TaskCellRenderer` (tachado y colores de estado obsoletos).
- `App` aplica el tema oscuro único; `LanternaTaskTrackerView` envía los avisos de
  arranque al banner como avisos (amarillo).
- Helpers puros testables: `CenterTruncator` (truncado central) y `ShortcutBar`
  (adaptación de atajos por ancho).

## Impact

- La vista única arranca siempre en tema oscuro, sin alternancia.
- La presentación pasa de tabla con columnas a lista con íconos y atenuado.
- El estado (selección, completado, borrado, purga, creación) sigue redibujando la
  vista sin contenido residual; los tests de ruteo de `TaskListWindow` siguen pasando.
- Verificación: `mvn test` y `mvn package`.
