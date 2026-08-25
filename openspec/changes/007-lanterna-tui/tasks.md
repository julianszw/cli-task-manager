# Tasks

- [x] Crear skill `lanterna` (`.agents/skills/lanterna/SKILL.md`)
- [x] Agregar `com.googlecode.lanterna:lanterna:3.1.5` y quitar `jline` en `pom.xml`
- [x] Crear `TaskTrackerView` y `CommandDispatcher`
- [x] Cambiar `Command.execute` para recibir `TaskTrackerView`
- [x] Actualizar comandos (`add`, `list`, `complete`, `purge`, `help`) y agregar `exit`
- [x] Crear `LanternaTaskTrackerView`, `MainWindow` y `TaskListWindow`
- [x] Reescribir `App` para usar Lanterna (terminal/screen/gui)
- [x] Eliminar `Key*`, `KeyReader`, `InteractiveTaskBrowser` y `TaskTableFormatter`
- [x] Actualizar tests (commands, dispatcher, task list) y eliminar tests obsoletos
- [x] Actualizar specs (`cli-interface`, `interactive-cli`, `output-formatting`) y `project.md`
- [x] Compilar y ejecutar tests (`mvn test`, `mvn package`)
