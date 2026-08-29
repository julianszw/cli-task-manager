# Add Task List Field

## Purpose
Define el tercer campo del formulario de nueva tarea: un campo que muestra la lista a la que se agregará la tarea. Por defecto muestra la lista activa y, al enfocarlo, la tecla `Tab` cicla la lista destino entre todas las listas existentes, de forma cíclica. Complementa el campo de título (`interactive-cli`) y el campo de fecha (`date-selection`).

## Requirements

### Requirement: Campo de lista en el formulario de nueva tarea
El sistema DEBE mostrar, en la ventana de nueva tarea, un tercer campo que indique la lista a la que se agregará la tarea.

#### Scenario: Campo visible
- **GIVEN** la ventana de nueva tarea abierta
- **WHEN** se muestra el formulario
- **THEN** se muestra un campo de lista además del campo de título y del campo de fecha

#### Scenario: Lista activa por defecto
- **GIVEN** la ventana de nueva tarea abierta sobre una lista activa
- **WHEN** se muestra el campo de lista
- **THEN** el campo muestra la lista activa como lista destino

### Requirement: Navegación del foco entre campos
El sistema DEBE permitir moverse entre los campos del formulario con `Tab` (avanzar) y `Shift+Tab` (retroceder).

#### Scenario: Avanzar con Tab
- **GIVEN** la ventana de nueva tarea abierta
- **WHEN** el usuario presiona `Tab` con el foco en el campo de título o de fecha
- **THEN** el foco avanza al siguiente campo (título → fecha → lista)

#### Scenario: Retroceder con Shift+Tab
- **GIVEN** la ventana de nueva tarea abierta
- **WHEN** el usuario presiona `Shift+Tab` con el foco en el campo de lista o de fecha
- **THEN** el foco retrocede al campo anterior (lista → fecha → título)

### Requirement: Ciclar la lista con Tab
Cuando el campo de lista tiene el foco, la tecla `Tab` DEBE cambiar la lista destino a la siguiente lista, de forma cíclica (al llegar a la última, vuelve a la primera). `Shift+Tab` DEBE sacar el foco del campo hacia el campo anterior, sin ciclar la lista.

#### Scenario: Avanzar a la siguiente lista
- **GIVEN** el campo de lista con el foco y dos o más listas existentes
- **WHEN** el usuario presiona `Tab`
- **THEN** la lista destino cambia a la siguiente lista

#### Scenario: Ciclo al llegar al final
- **GIVEN** el campo de lista con el foco y la lista destino es la última
- **WHEN** el usuario presiona `Tab`
- **THEN** la lista destino vuelve a la primera lista

#### Scenario: Una sola lista
- **GIVEN** el campo de lista con el foco y una única lista
- **WHEN** el usuario presiona `Tab`
- **THEN** la lista destino no cambia

#### Scenario: Shift+Tab sale del campo
- **GIVEN** el campo de lista con el foco
- **WHEN** el usuario presiona `Shift+Tab`
- **THEN** el foco vuelve al campo anterior (fecha)
- **AND** la lista destino no cambia

### Requirement: Incluir todas las listas
El sistema DEBE recorrer todas las listas existentes al ciclar el campo de lista, incluidas las listas vacías (aun cuando estén ocultas según `hide-empty-lists`).

#### Scenario: Lista vacía disponible
- **GIVEN** la opción de ocultado activa y una lista vacía oculta
- **WHEN** el usuario cicla el campo de lista con `Tab`
- **THEN** la lista vacía oculta aparece como destino disponible

### Requirement: Crear tarea en la lista elegida
Al confirmar la tarea, el sistema DEBE crearla en la lista destino seleccionada, que puede ser distinta de la lista activa.

#### Scenario: Crear en la lista destino elegida
- **GIVEN** el campo de lista con una lista destino distinta de la lista activa
- **WHEN** el usuario confirma la tarea
- **THEN** la tarea se crea en la lista destino elegida (según `task-management`)
- **AND** la tarea queda asociada a esa lista

#### Scenario: Crear en la lista activa por defecto
- **GIVEN** el campo de lista sin modificar (muestra la lista activa)
- **WHEN** el usuario confirma la tarea
- **THEN** la tarea se crea en la lista activa
