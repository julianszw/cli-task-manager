# Registro de commits por change

Fuente de verdad que mapea cada `openspec/changes/<nnn>-<id>/` al commit (o
commits) que lo implementó. El agente `commit` actualiza este archivo después de
cada commit.

> Formato de mensaje de commit: `openspec <nnn>-<id>: <resumen>`.

## Pendientes de commit

_(ninguno)_

## Commiteados

| Change | Commit (SHA) | Resumen |
|--------|--------------|---------|
| `001-implement-task-management-and-cli` | `58c144d` | implementar task-management y cli-interface |
| `002-purge-completed-tasks` | `efd7486` | agregar comando purge |
| `003-pretty-output-formatting` | `996fc9f` | formatear salida en tabla con color y tachado |
| `004-interactive-navigation` | `00a21ea` | agregar navegación interactiva para list |
| `005-package-runnable-jar` | `36ae571` | empaquetar jar ejecutable con shade |
| `006-interactive-help-and-back` | `43d760e` | agregar ayuda permanente y tecla atrás |
| `007-lanterna-tui` | `5e0e93d` | refactor de la capa de presentación a Lanterna |
| `008-json-persistence` | `f1adc60` | persistir tareas en archivo json |
| `009-navigable-main-menu` | `439d551` | agregar menú principal navegable |
| `010-launch-scripts` | `7f5559a` | agregar scripts de lanzamiento |
| `011-unified-task-view` | `3c0dc1a` | unificar vista de tareas y quitar menú principal |
| `012-dark-theme` | `bdbecaa` | agregar tema oscuro alternable |
| `013-remove-unused-code` | `1440fe4` | eliminar capa de comandos cli no usada |
| `014-visual-style` | `684ca4e` | implementar lenguaje visual agentty |
