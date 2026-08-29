# Date Selection

## Purpose
Define cómo se ingresa la fecha de vencimiento de una tarea: el campo de fecha se
precarga con la fecha de hoy y, al posicionarse sobre él, se abre un calendario
interactivo pequeño y navegable para elegir la fecha. Aplica a los campos de fecha
de las ventanas de nueva tarea, edición de tarea y edición de fecha.

## Requirements

### Requirement: Fecha de hoy por defecto
El sistema DEBE precargar el campo de fecha de vencimiento con la fecha de hoy
cuando la tarea no tiene una fecha establecida, de modo que una tarea nueva quede,
por defecto, con la fecha de hoy.

#### Scenario: Nueva tarea sin tocar la fecha
- **GIVEN** la ventana de nueva tarea abierta
- **WHEN** el usuario confirma la tarea sin modificar el campo de fecha
- **THEN** la tarea se crea con la fecha de hoy (según `task-management`)

#### Scenario: Campo de fecha precargado
- **GIVEN** la ventana de nueva tarea abierta
- **WHEN** el usuario se posiciona sobre el campo de fecha
- **THEN** el campo ya muestra la fecha de hoy

#### Scenario: Tarea existente con fecha
- **GIVEN** una tarea con fecha de vencimiento establecida
- **WHEN** se abre el campo de fecha para editarla
- **THEN** el campo muestra la fecha existente de la tarea
- **AND** no se sobrescribe con la fecha de hoy

#### Scenario: Tarea existente sin fecha
- **GIVEN** una tarea sin fecha de vencimiento
- **WHEN** se abre el campo de fecha para establecerla
- **THEN** el campo aparece precargado con la fecha de hoy

### Requirement: Calendario interactivo
El sistema DEBE mostrar un calendario interactivo pequeño y navegable al
posicionarse sobre el campo de fecha, para elegir la fecha.

#### Scenario: Abrir el calendario
- **GIVEN** un campo de fecha visible
- **WHEN** el usuario se posiciona sobre el campo de fecha
- **THEN** se abre un calendario interactivo pequeño (overlay)
- **AND** el calendario muestra el mes correspondiente a la fecha actual del campo, con el día actual resaltado

#### Scenario: Navegar entre días y meses
- **GIVEN** el calendario abierto
- **WHEN** el usuario navega con las flechas
- **THEN** la selección se mueve entre días
- **AND** el usuario puede desplazarse entre meses

#### Scenario: Elegir una fecha
- **GIVEN** el calendario abierto con un día seleccionado
- **WHEN** el usuario confirma el día con `Enter`
- **THEN** el calendario se cierra
- **AND** el campo de fecha queda con la fecha elegida

#### Scenario: Cancelar el calendario
- **GIVEN** el calendario abierto
- **WHEN** el usuario presiona `Esc`
- **THEN** el calendario se cierra sin modificar la fecha
- **AND** el campo conserva su valor previo

### Requirement: Navegación cíclica del calendario
La navegación del calendario DEBE ser cíclica: al pasar el último día de un mes la selección avanza al primer día del mes siguiente, y al pasar del último mes la selección vuelve al primer mes del año.

#### Scenario: Ciclo entre días
- **GIVEN** el calendario abierto con un día seleccionado
- **WHEN** el usuario navega más allá del último día del mes
- **THEN** la selección avanza al primer día del mes siguiente

#### Scenario: Ciclo entre meses
- **GIVEN** el calendario abierto en el último mes del año
- **WHEN** el usuario navega al mes siguiente
- **THEN** el calendario muestra el primer mes del año siguiente

### Requirement: Quitar fecha dejando el campo vacío
El sistema DEBE permitir dejar la fecha de vencimiento vacía (quitar la fecha)
desde el campo de fecha, aun cuando esté precargado con la fecha de hoy.

#### Scenario: Quitar fecha
- **GIVEN** una tarea con fecha de vencimiento
- **WHEN** el usuario deja el campo de fecha vacío y confirma
- **THEN** la tarea queda sin fecha de vencimiento (según `task-management`)
