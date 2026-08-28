# Navegación circular en el menú de acciones

## Why

La navegación del menú de acciones de tarea (`task-action-menu`) estaba acotada:
al llegar al final con `j` o al principio con `k`, la selección se detenía. El spec
actualizado exige que la navegación sea circular: desde la última acción se vuelve a
la primera y desde la primera se vuelve a la última.

## What Changes

- `TaskActionMenuWindow.moveSelection(int)`: aplica aritmética modular sobre el
  índice seleccionado para que la navegación envuelva en ambos sentidos.

## Impact

- Se implementa el requisito "Navegación circular entre acciones" de
  `task-action-menu`.
- Verificación: `mvn test`.
