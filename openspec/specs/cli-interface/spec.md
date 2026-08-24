# CLI Interface

## Purpose
Define cómo el usuario interactúa por terminal con las capacidades de `task-management`: comandos disponibles, formato de entrada/salida y manejo de errores de input.

## Requirements

### Requirement: Registro de comandos
El sistema DEBE exponer un conjunto fijo de comandos conocidos por nombre.

#### Scenario: Comando reconocido
- **GIVEN** un nombre de comando registrado (`add`, `list`, `complete`, `purge`)
- **WHEN** el usuario lo ingresa
- **THEN** el sistema ejecuta la acción correspondiente

#### Scenario: Comando no reconocido
- **GIVEN** un nombre de comando que no existe en el registro
- **WHEN** el usuario lo ingresa
- **THEN** el sistema muestra un mensaje de error indicando que el comando no existe
- **AND** no interrumpe la ejecución del programa

### Requirement: Comando add
El comando `add` DEBE crear una tarea a partir de los argumentos recibidos.

#### Scenario: Uso correcto
- **GIVEN** el comando `add` seguido de uno o más argumentos
- **WHEN** se ejecuta
- **THEN** los argumentos se unen como título de la tarea
- **AND** se muestra confirmación con el id generado

#### Scenario: Sin argumentos
- **GIVEN** el comando `add` sin argumentos
- **WHEN** se ejecuta
- **THEN** se muestra el uso correcto del comando
- **AND** no se crea ninguna tarea

### Requirement: Comando list
El comando `list` DEBE iniciar el modo de navegación interactiva definido en `interactive-cli`.

#### Scenario: Con tareas
- **GIVEN** tareas existentes
- **WHEN** se ejecuta `list`
- **THEN** se inicia el modo interactivo con la tabla de tareas

#### Scenario: Sin tareas
- **GIVEN** que no hay tareas
- **WHEN** se ejecuta `list`
- **THEN** el modo interactivo muestra un mensaje indicando que no hay tareas cargadas

### Requirement: Comando complete
El comando `complete` DEBE marcar como completada la tarea cuyo id se pasa como argumento.

#### Scenario: Id válido
- **GIVEN** el comando `complete` seguido de un id numérico existente
- **WHEN** se ejecuta
- **THEN** se muestra confirmación de que la tarea fue completada

#### Scenario: Id no numérico
- **GIVEN** el comando `complete` seguido de un argumento no numérico
- **WHEN** se ejecuta
- **THEN** se muestra un mensaje indicando que el id debe ser un número
- **AND** no se modifica ninguna tarea

#### Scenario: Id inexistente
- **GIVEN** el comando `complete` seguido de un id numérico que no existe
- **WHEN** se ejecuta
- **THEN** se muestra un mensaje indicando que no existe una tarea con ese id

### Requirement: Comando purge
El comando `purge` DEBE eliminar todas las tareas completadas.

#### Scenario: Con tareas completadas
- **GIVEN** tareas en estado `COMPLETED`
- **WHEN** se ejecuta `purge`
- **THEN** se eliminan las tareas completadas
- **AND** se muestra la lista de tareas eliminadas

#### Scenario: Sin tareas completadas
- **GIVEN** que no hay tareas completadas
- **WHEN** se ejecuta `purge`
- **THEN** se muestra un mensaje indicando que no hay tareas completadas para eliminar