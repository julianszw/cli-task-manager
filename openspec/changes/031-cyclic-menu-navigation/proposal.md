# 031 — Navegación cíclica en todos los menúes

## Why
La navegación por teclado de los menúes era inconsistente: el menú de acciones
ya era cíclico, pero el selector de listas de "mover", el diálogo de confirmación
de salida y (parcialmente) las flechas no hacían wrap en los extremos.

## What Changes
- `OptionMenuWindow` (usado por el selector "mover" y el diálogo de salida):
  la selección pasa de "bounded" a cíclica y se agrega manejo de
  `ArrowUp`/`ArrowDown` además de `j`/`k`.
- `TaskActionMenuWindow`: se agrega manejo de `ArrowUp`/`ArrowDown` ruteado al
  mismo `moveSelection` cíclico (hasta ahora solo `j`/`k`).
- `CalendarWindow`: la navegación ya era cíclica por aritmética de `LocalDate`;
  se agregan tests que fijan el wrap de días y meses.

## Impact
- Specs: deltas en `task-action-menu` (selector de listas), `interactive-cli`
  (diálogo de salida) y `date-selection` (calendario).
- Código: `cli` (`OptionMenuWindow`, `TaskActionMenuWindow`).
- Tests: `OptionMenuWindowTest`, `TaskActionMenuWindowTest`, `CalendarWindowTest`.
