# Tema oscuro alternable (con tecla `t`)

## Why

La vista única de tareas (`interactive-cli`) se mostraba siempre con el tema por
defecto de Lanterna, sin forma de alternar a un tema oscuro. La spec `dark-theme`
exige: arrancar en tema claro, poder alternar entre claro y oscuro con la tecla
`t` en caliente (sin reiniciar), mantener distinguibles los estados de las tareas
y reflejar la tecla `t` en la ayuda permanente.

## What Changes

- Nuevo `ThemeManager`: mantiene el estado del tema (`claro`/`oscuro`, por defecto
  claro) y devuelve el `Theme` de Lanterna correspondiente (construido con
  `SimpleTheme`, con `selected`/`active` invertidos para resaltar la selección).
- `TaskListWindow` acepta un `ThemeManager` y añade la tecla `t`: alterna el tema,
  lo aplica a la GUI (`gui.setTheme(...)`) y fuerza el repintado inmediato
  (`gui.updateScreen()`).
- `TaskCellRenderer` se vuelve sensible al tema: las tareas `PENDING` se muestran
  en azul sobre tema claro y en amarillo sobre tema oscuro; las `COMPLETED` se
  mantienen en verde y tachadas en ambos temas.
- La ayuda permanente incorpora la tecla `t` (tema).

## Impact

- La vista única arranca en tema claro y cambia a oscuro (y viceversa) al pulsar
  `t` sin reiniciar, aplicándose a toda la vista (tabla, mensajes y ayuda).
- Los estados `PENDING` y `COMPLETED` siguen distinguiéndose visualmente en ambos
  temas (según `output-formatting`).
- Verificación: `mvn test` y `mvn package`.
