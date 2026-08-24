# Task Management

## Purpose
Define el comportamiento esperado para crear, consultar y completar tareas, independientemente de cómo se invoquen (CLI u otro cliente futuro).

## Requirements

### Requirement: Crear tarea
El sistema DEBE permitir crear una tarea nueva a partir de un título no vacío.

#### Scenario: Crear tarea con título válido
- **GIVEN** un título de tarea no vacío
- **WHEN** se solicita crear la tarea
- **THEN** el sistema genera un id único para la tarea
- **AND** la tarea queda en estado `PENDING`
- **AND** la tarea queda disponible para ser listada

#### Scenario: Intentar crear tarea con título vacío
- **GIVEN** un título vacío o compuesto solo por espacios
- **WHEN** se solicita crear la tarea
- **THEN** el sistema rechaza la creación
- **AND** no se genera ninguna tarea nueva

### Requirement: Listar tareas
El sistema DEBE permitir obtener todas las tareas existentes.

#### Scenario: Listar con tareas cargadas
- **GIVEN** una o más tareas creadas previamente
- **WHEN** se solicita el listado
- **THEN** el sistema devuelve todas las tareas con su id, título y estado

#### Scenario: Listar sin tareas cargadas
- **GIVEN** que no hay tareas creadas
- **WHEN** se solicita el listado
- **THEN** el sistema devuelve una lista vacía

### Requirement: Completar tarea
El sistema DEBE permitir marcar una tarea existente como completada.

#### Scenario: Completar tarea existente
- **GIVEN** una tarea con id válido en estado `PENDING`
- **WHEN** se solicita completar esa tarea
- **THEN** el estado de la tarea cambia a `COMPLETED`

#### Scenario: Completar tarea inexistente
- **GIVEN** un id que no corresponde a ninguna tarea
- **WHEN** se solicita completar esa tarea
- **THEN** el sistema lanza `TaskNotFoundException`
- **AND** ninguna tarea cambia de estado

#### Scenario: Completar tarea ya completada
- **GIVEN** una tarea con id válido en estado `COMPLETED`
- **WHEN** se solicita completar esa tarea nuevamente
- **THEN** el sistema deja la tarea en estado `COMPLETED` sin error
  (o alternativamente, lanza una excepción de estado inválido — a definir)

### Requirement: Eliminar tareas completadas
El sistema DEBE permitir eliminar todas las tareas que estén en estado `COMPLETED`.

#### Scenario: Eliminar con tareas completadas
- **GIVEN** una o más tareas en estado `COMPLETED`
- **WHEN** se solicita eliminar las tareas completadas
- **THEN** el sistema elimina las tareas completadas
- **AND** devuelve las tareas eliminadas
- **AND** las tareas en estado `PENDING` permanecen disponibles para ser listadas

#### Scenario: Eliminar sin tareas completadas
- **GIVEN** que no hay tareas en estado `COMPLETED`
- **WHEN** se solicita eliminar las tareas completadas
- **THEN** el sistema no elimina ninguna tarea
- **AND** devuelve una lista vacía