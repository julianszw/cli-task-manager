# Task Management

## Purpose
Define el comportamiento esperado para crear, consultar, completar y reabrir tareas, independientemente de cómo se invoquen (CLI u otro cliente futuro).

## Requirements

### Requirement: Crear tarea
El sistema DEBE permitir crear una tarea nueva a partir de un título no vacío,
asociada a una lista existente.

#### Scenario: Crear tarea con título válido
- **GIVEN** un título de tarea no vacío y una lista existente
- **WHEN** se solicita crear la tarea
- **THEN** el sistema genera un id único para la tarea
- **AND** la tarea queda en estado `PENDING`
- **AND** la tarea queda asociada a la lista indicada
- **AND** la tarea queda disponible para ser listada

#### Scenario: Crear tarea en una lista inexistente
- **GIVEN** un id de lista que no corresponde a ninguna lista
- **WHEN** se solicita crear la tarea
- **THEN** el sistema lanza una excepción de lista no encontrada
- **AND** no se genera ninguna tarea nueva

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

### Requirement: Reabrir tarea
El sistema DEBE permitir marcar una tarea existente en estado `COMPLETED` como pendiente nuevamente (reabrirla).

#### Scenario: Reabrir tarea completada
- **GIVEN** una tarea con id válido en estado `COMPLETED`
- **WHEN** se solicita reabrir esa tarea
- **THEN** el estado de la tarea cambia a `PENDING`

#### Scenario: Reabrir tarea inexistente
- **GIVEN** un id que no corresponde a ninguna tarea
- **WHEN** se solicita reabrir esa tarea
- **THEN** el sistema lanza `TaskNotFoundException`
- **AND** ninguna tarea cambia de estado

#### Scenario: Reabrir tarea pendiente
- **GIVEN** una tarea con id válido en estado `PENDING`
- **WHEN** se solicita reabrir esa tarea nuevamente
- **THEN** el sistema deja la tarea en estado `PENDING` sin error

### Requirement: Actualizar título de tarea
El sistema DEBE permitir modificar el título de una tarea existente.

#### Scenario: Actualizar título válido
- **GIVEN** una tarea con id válido y un título nuevo no vacío
- **WHEN** se solicita actualizar el título de esa tarea
- **THEN** el título de la tarea cambia al nuevo valor
- **AND** el resto de los atributos (id y estado) permanecen sin cambios

#### Scenario: Actualizar con título vacío
- **GIVEN** una tarea con id válido y un título nuevo vacío o compuesto solo por espacios
- **WHEN** se solicita actualizar el título de esa tarea
- **THEN** el sistema rechaza la actualización
- **AND** el título de la tarea no cambia

#### Scenario: Actualizar tarea inexistente
- **GIVEN** un id que no corresponde a ninguna tarea
- **WHEN** se solicita actualizar el título de esa tarea
- **THEN** el sistema lanza `TaskNotFoundException`
- **AND** ninguna tarea cambia de título

### Requirement: Eliminar tarea
El sistema DEBE permitir eliminar una tarea existente por su id.

#### Scenario: Eliminar tarea existente
- **GIVEN** una tarea con id válido
- **WHEN** se solicita eliminar esa tarea
- **THEN** la tarea se elimina
- **AND** deja de estar disponible para ser listada

#### Scenario: Eliminar tarea inexistente
- **GIVEN** un id que no corresponde a ninguna tarea
- **WHEN** se solicita eliminar esa tarea
- **THEN** el sistema lanza `TaskNotFoundException`
- **AND** ninguna tarea se elimina

### Requirement: Eliminar tareas completadas de una lista
El sistema DEBE permitir eliminar todas las tareas que estén en estado `COMPLETED`
dentro de una lista concreta.

#### Scenario: Eliminar con tareas completadas
- **GIVEN** una o más tareas en estado `COMPLETED` en la lista indicada
- **WHEN** se solicita eliminar las tareas completadas de esa lista
- **THEN** el sistema elimina las tareas completadas de esa lista
- **AND** devuelve las tareas eliminadas
- **AND** las tareas `PENDING` y las tareas de otras listas permanecen disponibles

#### Scenario: Eliminar sin tareas completadas
- **GIVEN** que no hay tareas en estado `COMPLETED` en la lista indicada
- **WHEN** se solicita eliminar las tareas completadas de esa lista
- **THEN** el sistema no elimina ninguna tarea
- **AND** devuelve una lista vacía

### Requirement: Mover tarea a otra lista
El sistema DEBE permitir mover una tarea existente de su lista actual a otra lista existente.

#### Scenario: Mover tarea a otra lista
- **GIVEN** una tarea con id válido asociada a una lista y una lista destino existente distinta de la actual
- **WHEN** se solicita mover la tarea a la lista destino
- **THEN** la tarea queda asociada a la lista destino
- **AND** deja de pertenecer a la lista original

#### Scenario: Mover a una lista inexistente
- **GIVEN** una tarea con id válido y un id de lista destino que no corresponde a ninguna lista
- **WHEN** se solicita mover la tarea
- **THEN** el sistema lanza una excepción de lista no encontrada
- **AND** la tarea permanece en su lista original

#### Scenario: Mover tarea inexistente
- **GIVEN** un id de tarea que no corresponde a ninguna tarea y una lista destino existente
- **WHEN** se solicita mover la tarea
- **THEN** el sistema lanza `TaskNotFoundException`
- **AND** ninguna tarea cambia de lista

#### Scenario: Mover a la misma lista
- **GIVEN** una tarea con id válido y una lista destino igual a su lista actual
- **WHEN** se solicita mover la tarea
- **THEN** la tarea permanece en la misma lista sin cambios
- **AND** no se produce ningún error