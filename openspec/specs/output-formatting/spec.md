# Output Formatting

## Purpose
Define cómo se presentan las tareas en la vista: una lista con ícono de estado por fila, orden por estado (pendientes primero, completadas al final), tachado y color gris para las tareas completadas, y colores para distinguir estados. Reemplaza el formato de tabla anterior (columnas `ID`, `STATUS` y `TITLE`), siguiendo el lenguaje visual de `visual-style`.

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

### Requirement: Formato de tareas completadas
El sistema DEBE mostrar toda tarea en estado `COMPLETED` con el ícono `✓` en verde, el título tachado y en color gris.

#### Scenario: Tarea completada
- **GIVEN** una tarea en estado `COMPLETED`
- **WHEN** se muestra en la lista
- **THEN** el ícono `✓` se muestra en verde
- **AND** el título se muestra tachado
- **AND** el título se muestra en color gris

#### Scenario: Tarea pendiente
- **GIVEN** una tarea en estado `PENDING`
- **WHEN** se muestra en la lista
- **THEN** la fila se muestra sin tachado
- **AND** sin el color gris de completada

### Requirement: Orden de tareas por estado
El sistema DEBE mostrar las tareas agrupadas por estado: las tareas `PENDING` primero y las tareas `COMPLETED` al final.

#### Scenario: Tarea completada se mueve al final
- **GIVEN** una tarea en estado `PENDING` ubicada entre otras tareas
- **WHEN** la tarea pasa a `COMPLETED`
- **THEN** la tarea se muestra al final de la lista, después de todas las tareas pendientes

#### Scenario: Tarea reabierta vuelve a las pendientes
- **GIVEN** una tarea en estado `COMPLETED` ubicada al final de la lista
- **WHEN** la tarea se reabre (pasa a `PENDING`)
- **THEN** la tarea deja de mostrarse al final y vuelve a agruparse con las pendientes

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
