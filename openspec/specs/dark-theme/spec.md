# Dark Theme

## Purpose
Define el tema oscuro de la interfaz y el mecanismo para alternarlo de forma rápida con el tema claro, sin reiniciar la aplicación. El tema se aplica de manera consistente a toda la vista única definida en `interactive-cli`.

## Requirements

### Requirement: Alternancia en caliente
El sistema DEBE permitir alternar entre el tema claro y el tema oscuro con la tecla `t` mientras la aplicación está en ejecución.

#### Scenario: Alternar a oscuro
- **GIVEN** la aplicación con el tema claro activo
- **WHEN** el usuario presiona `t`
- **THEN** la vista cambia al tema oscuro
- **AND** el cambio se aplica de inmediato, sin reiniciar

#### Scenario: Alternar a claro
- **GIVEN** la aplicación con el tema oscuro activo
- **WHEN** el usuario presiona `t`
- **THEN** la vista cambia al tema claro

### Requirement: Tema por defecto
El sistema DEBE iniciar con el tema claro.

#### Scenario: Inicio en tema claro
- **GIVEN** la aplicación iniciada
- **WHEN** se muestra la vista
- **THEN** la vista se muestra con el tema claro

### Requirement: Apariencia del tema oscuro
El sistema DEBE mostrar, con el tema oscuro, un fondo oscuro y texto claro, manteniendo distinguibles los estados de las tareas.

#### Scenario: Fondo y texto oscuros
- **GIVEN** el tema oscuro activo
- **WHEN** se muestra la vista
- **THEN** el fondo es oscuro
- **AND** el texto se muestra en tonos claros

#### Scenario: Estados distinguibles
- **GIVEN** el tema oscuro activo con tareas en estados `PENDING` y `COMPLETED`
- **WHEN** se muestra la lista
- **THEN** cada estado se distingue visualmente del otro (según `output-formatting`)

### Requirement: Aplicación consistente del tema
El sistema DEBE aplicar el tema activo a todos los elementos de la vista única.

#### Scenario: Tema en toda la vista
- **GIVEN** un tema activo
- **WHEN** se muestra la vista (tabla, mensajes, ayuda de teclas o campo de entrada)
- **THEN** todos los elementos se muestran con el tema activo

#### Scenario: Cambiar de tema no altera el estado
- **GIVEN** la vista con una tarea seleccionada
- **WHEN** el usuario alterna el tema con `t`
- **THEN** la selección y las tareas se mantienen sin cambios

### Requirement: Tecla de tema en la ayuda
El sistema DEBE indicar la tecla de alternancia de tema en la ayuda permanente de teclas.

#### Scenario: Ayuda con tecla de tema
- **GIVEN** la vista única visible
- **WHEN** se muestra la ayuda de teclas
- **THEN** la ayuda incluye la tecla `t` (tema)
