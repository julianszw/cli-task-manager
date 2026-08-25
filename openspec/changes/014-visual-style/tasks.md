# Tasks

- [x] Crear `VisualStyle` (paleta + `Theme` oscuro único) y eliminar `ThemeManager`
- [x] Crear `MessageKind` (INFO/WARN/ERROR) para el banner
- [x] Crear `CenterTruncator` (truncado central de título)
- [x] Crear `ShortcutBar` (fila de atajos adaptativa por ancho)
- [x] Crear `TaskViewComponent` (lista con íconos + barra de estado con render custom)
- [x] Crear `RoundedBorder` (borde redondeado `╭` `─` `╮` `│` `╰` `╯`)
- [x] Reescribir `TaskListWindow` (quitar `t` y `Table`; selección propia + flechas)
- [x] Eliminar `TaskCellRenderer`
- [x] `AddTaskWindow` con borde redondeado y error `⚠` en rojo
- [x] `App` aplica el tema oscuro único; `LanternaTaskTrackerView` envía avisos al banner
- [x] Actualizar specs (`dark-theme`/`main-menu`/`cli-interface` removidas, `output-formatting` reescrita, `visual-style` nueva)
- [x] Actualizar tests (quitar `ThemeManagerTest` y `t`; añadir `VisualStyleTest`, `CenterTruncatorTest`, `ShortcutBarTest`)
- [x] Actualizar `project.md` y `README.md`
- [x] Compilar y ejecutar tests (`mvn test`, `mvn package`)
