# Main Menu

## Purpose
Define el menú principal de la variante TUI-DOS: una lista de opciones
seleccionables, navegable con las flechas y activable por atajo de teclado
(estilo DOS) o con `Enter`. Reemplaza al prompt de texto libre.

## Requirements

### Requirement: Lista de opciones
El sistema DEBE mostrar un menú principal con las opciones `Add task`,
`List tasks`, `Complete task`, `Reopen task`, `Purge completed`, `Help`
y `Exit`.

#### Scenario: Menú visible al iniciar
- **GIVEN** la aplicación iniciada
- **WHEN** se muestra la ventana principal
- **THEN** se listan las opciones `Add task`, `List tasks`, `Complete task`,
  `Reopen task`, `Purge completed`, `Help` y `Exit`
- **AND** una de las opciones aparece seleccionada por defecto

### Requirement: Atajos de teclado por opción
El sistema DEBE asignar a cada opción un atajo de una letra que la activa
directamente, indicado visualmente en la opción.

#### Scenario: Atajos asignados
- **GIVEN** el menú principal visible
- **WHEN** se muestran las opciones
- **THEN** cada opción indica su atajo: `a` (`Add task`), `l` (`List tasks`),
  `c` (`Complete task`), `r` (`Reopen task`), `p` (`Purge completed`),
  `h` (`Help`) y `x` (`Exit`)

#### Scenario: Activar con el atajo
- **GIVEN** el menú principal visible
- **WHEN** el usuario presiona la letra de atajo de una opción (por ejemplo `l`)
- **THEN** se ejecuta la acción asociada a esa opción
- **AND** no es necesario mover la selección hasta esa opción

### Requirement: Navegación con flechas
El sistema DEBE permitir moverse entre opciones con `↑`/`k` (arriba) y `↓`/`j`
(abajo), manteniendo visible la opción seleccionada.

#### Scenario: Mover selección
- **GIVEN** el menú principal visible
- **WHEN** el usuario presiona `↓` o `j`
- **THEN** la selección se mueve a la siguiente opción
- **AND** la opción seleccionada se resalta visualmente

#### Scenario: Límites del menú
- **GIVEN** la selección en la primera o última opción
- **WHEN** el usuario intenta moverse más allá del límite
- **THEN** la selección se mantiene sin salir del menú

### Requirement: Activación con Enter
El sistema DEBE ejecutar la opción seleccionada al presionar `Enter`.

#### Scenario: Ejecutar selección
- **GIVEN** una opción seleccionada en el menú
- **WHEN** el usuario presiona `Enter`
- **THEN** se ejecuta la acción asociada a la opción seleccionada

### Requirement: Sin prompt de texto
El sistema NO DEBE mostrar un prompt de texto libre para ingresar comandos.

#### Scenario: Interacción solo por menú
- **GIVEN** la variante TUI-DOS en ejecución
- **WHEN** se muestra el menú principal
- **THEN** no hay un campo de texto para escribir comandos
- **AND** la interacción se realiza mediante las opciones, sus atajos y `Enter`

### Requirement: Opción Add task
La opción `Add task` DEBE permitir crear una tarea a partir de un título
ingresado por el usuario.

#### Scenario: Crear tarea
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `Add task` (con `a` o `Enter`)
- **THEN** el sistema solicita el título de la tarea
- **AND** al confirmar un título no vacío, se crea la tarea (según
  `task-management`)

#### Scenario: Título vacío
- **GIVEN** la opción `Add task` activada
- **WHEN** el usuario confirma un título vacío o solo con espacios
- **THEN** no se crea ninguna tarea
- **AND** se muestra un mensaje de error

### Requirement: Opción List tasks
La opción `List tasks` DEBE abrir la vista interactiva de tareas definida en
`interactive-cli`.

#### Scenario: Abrir lista de tareas
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `List tasks` (con `l` o `Enter`)
- **THEN** se abre la vista interactiva de tareas

### Requirement: Opción Complete task
La opción `Complete task` DEBE permitir seleccionar una tarea para marcarla
como completada.

#### Scenario: Completar tarea
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `Complete task` (con `c` o `Enter`)
- **THEN** se muestra la lista de tareas para seleccionar una
- **AND** la tarea seleccionada se marca como `COMPLETED`

### Requirement: Opción Reopen task
La opción `Reopen task` DEBE permitir seleccionar una tarea completada para
volverla a estado `PENDING`.

#### Scenario: Reabrir tarea
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `Reopen task` (con `r` o `Enter`)
- **THEN** se muestra la lista de tareas para seleccionar una
- **AND** la tarea seleccionada vuelve a estado `PENDING`

### Requirement: Opción Purge completed
La opción `Purge completed` DEBE eliminar todas las tareas completadas.

#### Scenario: Purgar completadas
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `Purge completed` (con `p` o `Enter`)
- **THEN** se eliminan todas las tareas en estado `COMPLETED`
- **AND** se muestra el resultado de la operación

### Requirement: Opción Help
La opción `Help` DEBE mostrar la ayuda con las opciones disponibles y sus
atajos.

#### Scenario: Mostrar ayuda
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `Help` (con `h` o `Enter`)
- **THEN** se muestra la lista de opciones disponibles con sus atajos

### Requirement: Opción Exit
La opción `Exit` DEBE cerrar la aplicación.

#### Scenario: Salir de la aplicación
- **GIVEN** el menú principal visible
- **WHEN** el usuario activa `Exit` (con `x` o `Enter`)
- **THEN** se cierra la aplicación
