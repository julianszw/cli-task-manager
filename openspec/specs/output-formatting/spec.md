# Output Formatting

## Purpose
Define cómo se formatea por terminal la salida que muestra tareas: tabla con
columnas alineadas, tachado de las filas completadas y colores para distinguir
estados. La tabla se renderiza con el componente `Table` de Lanterna y se estiliza
mediante un `TableCellRenderer` (colores `TextColor` y `SGR.CROSSED_OUT`).

## Requirements

### Requirement: Formato de tabla
El sistema DEBE mostrar las tareas en formato de tabla con columnas alineadas para id, estado y título.

#### Scenario: Tabla con tareas
- **GIVEN** una o más tareas existentes
- **WHEN** se muestra el listado de tareas
- **THEN** las tareas se presentan como filas de una tabla
- **AND** las columnas id, estado y título quedan alineadas
- **AND** la tabla incluye un encabezado que identifica cada columna

#### Scenario: Tabla sin tareas
- **GIVEN** que no hay tareas
- **WHEN** se muestra el listado de tareas
- **THEN** no se renderiza ninguna fila de datos
- **AND** se muestra un mensaje indicando que no hay tareas

### Requirement: Tachado de tareas completadas
El sistema DEBE tachar la fila completa de toda tarea en estado `COMPLETED`.

#### Scenario: Tarea completada
- **GIVEN** una tarea en estado `COMPLETED`
- **WHEN** se muestra en la tabla
- **THEN** toda la fila (id, estado y título) se muestra tachada

#### Scenario: Tarea pendiente
- **GIVEN** una tarea en estado `PENDING`
- **WHEN** se muestra en la tabla
- **THEN** la fila se muestra sin tachado

### Requirement: Color por estado
El sistema DEBE distinguir visualmente el estado de cada tarea mediante colores.

#### Scenario: Distinción de estados
- **GIVEN** tareas en distintos estados (`PENDING` y `COMPLETED`)
- **WHEN** se muestra el listado
- **THEN** cada estado se muestra con un color que lo distingue del otro

### Requirement: Aplicación consistente del formato
El sistema DEBE aplicar el formato de tabla, tachado y color en la vista
interactiva de tareas (la vista principal que lista tareas).

#### Scenario: Vista interactiva de tareas
- **GIVEN** el modo interactivo activo con tareas
- **WHEN** se muestra la tabla
- **THEN** las filas siguen el formato de tabla, tachado y color definido

#### Scenario: Resumen de tareas eliminadas
- **GIVEN** un comando cuya salida reporta tareas eliminadas (por ejemplo `purge`)
- **WHEN** se ejecuta
- **THEN** se muestra un resumen con las tareas eliminadas (id y título)
