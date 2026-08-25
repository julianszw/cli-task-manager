# Convenciones del proyecto

## Registro de cambios (openspec/changes)

Cada cambio en el código DEBE quedar reflejado en `openspec/changes/` antes de
darse por terminado. Aplica siempre, incluso para correcciones pequeñas.

Para registrar un cambio:

1. Crea un directorio `openspec/changes/<nnn>-<id>/` con `<nnn>` como número
   secuencial de tres dígitos según el orden de creación (ej. `001-`) y
   `<id>` en kebab-case descriptivo (ej. `001-implement-task-management-and-cli`).
   El número se incrementa respecto al último cambio existente.
2. Añade `proposal.md` con las secciones `Why`, `What Changes` e `Impact`.
3. Añade `tasks.md` con la checklist de tareas (`- [x]` para lo completado,
   `- [ ]` para lo pendiente).
4. Si el cambio modifica o agrega una capacidad, refleja el delta en
   `openspec/specs/` o como delta dentro del propio directorio del cambio.

Solo el agente `git` está habilitado para ejecutar comandos de git. La fuente
de verdad de qué changes ya fueron commiteados es `openspec/changes/REGISTER.md`
(mensajes de commit con prefijo `openspec <nnn>-<id>: <resumen>`).
