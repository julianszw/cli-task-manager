# Output Formatting

## Purpose
Define cómo se presentan las tareas en la vista: una lista con ícono de estado por fila, atenuado de las tareas completadas y colores para distinguir estados. Reemplaza el formato de tabla anterior (columnas `ID`, `STATUS` y `TITLE`) y el tachado, siguiendo el lenguaje visual de `visual-style`.

## Requirements

### Requirement: Lista de tareas con ícono de estado
El sistema DEBE mostrar las tareas como una lista, con un ícono de estado al inicio de cada fila.

#### Scenario: Lista con tareas
- **GIVEN** una o más tareas existentes
- **WHEN** se muestra el listado de tareas
- **THEN** cada tarea se presenta como una fila de la lista
- **AND** cada fila muestra el título de la tarea

#### Scenario: Lista sin tareas
- **GIVEN** que no hay tareas
- **WHEN** se muestra el listado de tareas
- **THEN** no se renderiza ninguna fila de datos
- **AND** se muestra un mensaje indicando que no hay tareas

### Requirement: Íconos y color por estado
El sistema DEBE distinguir el estado de cada tarea mediante un ícono y un color.

#### Scenario: Distinción de estados
- **GIVEN** tareas en estados `PENDING` y `COMPLETED`
- **WHEN** se muestra el listado
- **THEN** la tarea `PENDING` se muestra con el ícono `○`
- **AND** la tarea `COMPLETED` se muestra con el ícono `✓` en verde

### Requirement: Atenuado de tareas completadas
El sistema DEBE atenuar la fila completa de toda tarea en estado `COMPLETED`, sin tacharla.

#### Scenario: Tarea completada
- **GIVEN** una tarea en estado `COMPLETED`
- **WHEN** se muestra en la lista
- **THEN** toda la fila (ícono y título) se muestra atenuada
- **AND** la fila no se muestra tachada

#### Scenario: Tarea pendiente
- **GIVEN** una tarea en estado `PENDING`
- **WHEN** se muestra en la lista
- **THEN** la fila se muestra sin atenuar

### Requirement: Aplicación consistente del formato
El sistema DEBE aplicar el formato de lista, íconos, atenuado y color en la vista interactiva de tareas (la vista principal que lista tareas).

#### Scenario: Vista interactiva de tareas
- **GIVEN** el modo interactivo activo con tareas
- **WHEN** se muestra la lista
- **THEN** las filas siguen el formato de lista, íconos, atenuado y color definido

#### Scenario: Resumen de tareas eliminadas
- **GIVEN** un comando cuya salida reporta tareas eliminadas (por ejemplo `purge`)
- **WHEN** se ejecuta
- **THEN** se muestra un resumen con las tareas eliminadas (`ID` y `TITLE`)
