# Interactive CLI

## Purpose
Define el modo de navegación interactiva de la terminal, que reemplaza al comando `list`: permite recorrer, completar, eliminar y purgar tareas mediante teclas, mostrando la salida con el formato definido en `output-formatting`.

## Requirements

### Requirement: Activación del modo interactivo
El comando `list` DEBE iniciar el modo de navegación interactiva en lugar de imprimir un listado estático.

#### Scenario: Ejecutar list
- **GIVEN** el comando `list` ingresado
- **WHEN** se ejecuta
- **THEN** se inicia el modo interactivo
- **AND** se muestra la tabla de tareas con la primera tarea seleccionada

#### Scenario: Sin tareas
- **GIVEN** que no hay tareas
- **WHEN** se inicia el modo interactivo
- **THEN** se muestra un mensaje indicando que no hay tareas
- **AND** el usuario puede salir del modo

### Requirement: Navegación por teclado
El sistema DEBE permitir moverse por la lista de tareas con las teclas `↑`/`k` (arriba) y `↓`/`j` (abajo), manteniendo visible la tarea seleccionada.

#### Scenario: Mover selección
- **GIVEN** el modo interactivo activo con una o más tareas
- **WHEN** el usuario presiona `↓` o `j`
- **THEN** la selección se mueve a la siguiente tarea
- **AND** la selección se resalta visualmente

#### Scenario: Límites de la lista
- **GIVEN** la selección en la primera o última tarea
- **WHEN** el usuario intenta moverse más allá del límite
- **THEN** la selección se mantiene sin salir de la lista

### Requirement: Completar tarea seleccionada
La tecla `c` DEBE marcar como completada la tarea seleccionada.

#### Scenario: Completar selección pendiente
- **GIVEN** una tarea seleccionada en estado `PENDING`
- **WHEN** el usuario presiona `c`
- **THEN** la tarea pasa a `COMPLETED`
- **AND** la vista se actualiza aplicando tachado y color según `output-formatting`

#### Scenario: Completar selección ya completada
- **GIVEN** una tarea seleccionada en estado `COMPLETED`
- **WHEN** el usuario presiona `c`
- **THEN** no se produce ningún cambio ni error

### Requirement: Eliminar tarea seleccionada
La tecla `d` DEBE eliminar la tarea seleccionada.

#### Scenario: Eliminar selección
- **GIVEN** una tarea seleccionada
- **WHEN** el usuario presiona `d`
- **THEN** la tarea se elimina
- **AND** la vista se actualiza sin mostrar la tarea eliminada

### Requirement: Purgar tareas completadas
La tecla `p` DEBE eliminar todas las tareas en estado `COMPLETED`.

#### Scenario: Con tareas completadas
- **GIVEN** una o más tareas en estado `COMPLETED`
- **WHEN** el usuario presiona `p`
- **THEN** se eliminan todas las tareas completadas
- **AND** la vista se actualiza mostrando solo las tareas pendientes

#### Scenario: Sin tareas completadas
- **GIVEN** que no hay tareas en estado `COMPLETED`
- **WHEN** el usuario presiona `p`
- **THEN** no se elimina ninguna tarea
- **AND** se muestra un mensaje indicando que no hay tareas completadas

### Requirement: Salir del modo interactivo
Las teclas `q` y `Esc` DEBEN salir del modo interactivo.

#### Scenario: Salir
- **GIVEN** el modo interactivo activo
- **WHEN** el usuario presiona `q` o `Esc`
- **THEN** se sale del modo interactivo
- **AND** se vuelve al prompt de comandos
