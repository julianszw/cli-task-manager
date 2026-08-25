# Unified Task View

## Purpose
Define la vista única de la aplicación: una lista de tareas siempre visible que se muestra al iniciar la aplicación y sobre la que se navega y se ejecutan acciones (completar, reabrir, eliminar, purgar y crear) mediante atajos de teclado. Reemplaza al menú principal (`main-menu`): ya no existe una pantalla separada de opciones.

## Requirements

### Requirement: Vista única al iniciar
La aplicación DEBE abrir directamente en la vista de lista de tareas, sin un menú principal separado.

#### Scenario: Apertura de la aplicación
- **GIVEN** la aplicación iniciada
- **WHEN** se muestra la ventana principal
- **THEN** se muestra la lista de tareas
- **AND** no se muestra un menú principal de opciones

#### Scenario: Sin tareas al iniciar
- **GIVEN** que no hay tareas
- **WHEN** se muestra la vista
- **THEN** se muestra un mensaje indicando que no hay tareas
- **AND** el usuario puede crear una tarea o salir de la aplicación

### Requirement: Navegación por teclado
El sistema DEBE permitir moverse por la lista de tareas con las teclas `↑`/`k` (arriba) y `↓`/`j` (abajo), manteniendo visible la tarea seleccionada.

#### Scenario: Mover selección
- **GIVEN** la vista activa con una o más tareas
- **WHEN** el usuario presiona `↓` o `j`
- **THEN** la selección se mueve a la siguiente tarea
- **AND** la selección se resalta visualmente

#### Scenario: Límites de la lista
- **GIVEN** la selección en la primera o última tarea
- **WHEN** el usuario intenta moverse más allá del límite
- **THEN** la selección se mantiene sin salir de la lista

### Requirement: Redibujado de la vista al cambiar el estado
El sistema DEBE redibujar la vista cada vez que cambia el estado (selección, completado, borrado, purga o creación), sin acumular contenido residual en pantalla.

#### Scenario: Navegar entre tareas
- **GIVEN** la vista activa con una o más tareas
- **WHEN** el usuario cambia la selección con `↑`/`k` o `↓`/`j`
- **THEN** la vista se redibuja con la nueva tarea seleccionada
- **AND** no queda contenido residual en pantalla

### Requirement: Completar tarea seleccionada
La tecla `c` DEBE marcar como completada la tarea seleccionada.

#### Scenario: Completar selección pendiente
- **GIVEN** una tarea seleccionada en estado `PENDING`
- **WHEN** el usuario presiona `c`
- **THEN** la tarea pasa a `COMPLETED`
- **AND** la vista se actualiza aplicando atenuado y color según `output-formatting`

#### Scenario: Completar selección ya completada
- **GIVEN** una tarea seleccionada en estado `COMPLETED`
- **WHEN** el usuario presiona `c`
- **THEN** no se produce ningún cambio ni error

### Requirement: Reabrir tarea seleccionada
La tecla `r` DEBE volver a estado `PENDING` (reabrir) la tarea seleccionada.

#### Scenario: Reabrir selección completada
- **GIVEN** una tarea seleccionada en estado `COMPLETED`
- **WHEN** el usuario presiona `r`
- **THEN** la tarea pasa a `PENDING`
- **AND** la vista se actualiza aplicando el formato de tarea pendiente según `output-formatting`

#### Scenario: Reabrir selección ya pendiente
- **GIVEN** una tarea seleccionada en estado `PENDING`
- **WHEN** el usuario presiona `r`
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

### Requirement: Crear tarea desde la vista
La tecla `a` DEBE abrir un campo de entrada para crear una tarea.

#### Scenario: Crear tarea
- **GIVEN** la vista única visible
- **WHEN** el usuario presiona `a`
- **THEN** se abre un campo de entrada para ingresar el título
- **AND** al confirmar un título no vacío, se crea la tarea (según `task-management`)
- **AND** la vista se actualiza mostrando la tarea creada

#### Scenario: Título vacío
- **GIVEN** el campo de entrada abierto
- **WHEN** el usuario confirma un título vacío o compuesto solo por espacios
- **THEN** no se crea ninguna tarea
- **AND** se muestra un mensaje de error

### Requirement: Ayuda permanente de teclas
El sistema DEBE mostrar siempre una ayuda visible con las teclas disponibles, en la fila de atajos de la barra de estado (según `visual-style`).

#### Scenario: Ayuda siempre visible
- **GIVEN** la vista única visible
- **WHEN** se muestra la vista
- **THEN** se muestra la fila de atajos con las teclas disponibles: `↑`/`k` (up), `↓`/`j` (down), `a` (add), `c` (complete), `r` (reopen), `d` (delete), `p` (purge), `q`/`Esc` (exit)
- **AND** la ayuda permanece visible en todo momento, sin necesidad de presionar una tecla para mostrarla

### Requirement: Salir de la aplicación
Las teclas `q` y `Esc` DEBEN cerrar la aplicación.

#### Scenario: Salir
- **GIVEN** la vista única visible
- **WHEN** el usuario presiona `q` o `Esc`
- **THEN** se cierra la aplicación
- **AND** no hay un menú al que volver
