# UI Zoom

## Purpose
Define la posibilidad de hacer zoom visual global sobre la vista única de tareas (`interactive-cli`): escalar la interfaz en niveles discretos mediante atajos de teclado, restablecer al nivel por defecto y persistir la elección entre sesiones (junto con el estado en el archivo JSON según `json-persistence`).

## Requirements

### Requirement: Niveles de zoom discretos
El sistema DEBE ofrecer cinco niveles de zoom discretos — `-2`, `-1`, `0`, `+1` y `+2` — con `0` como nivel por defecto.

#### Scenario: Nivel por defecto
- **GIVEN** la aplicación iniciada sin un nivel de zoom persistido
- **WHEN** se muestra la vista
- **THEN** la vista se muestra en el nivel `0`

#### Scenario: Nivel dentro del rango
- **GIVEN** la vista visible
- **WHEN** se muestra la vista
- **THEN** el nivel de zoom se encuentra siempre dentro del rango `-2` a `+2`

### Requirement: Aumentar zoom
El atajo `Ctrl+=` DEBE aumentar el nivel de zoom en un paso.

#### Scenario: Aumentar desde el nivel por defecto
- **GIVEN** la vista visible en el nivel `0`
- **WHEN** el usuario presiona `Ctrl+=`
- **THEN** el nivel de zoom pasa a `+1`
- **AND** la interfaz se muestra más ampliada (texto y elementos más grandes)

#### Scenario: Aumentar en el máximo
- **GIVEN** la vista visible en el nivel `+2`
- **WHEN** el usuario presiona `Ctrl+=`
- **THEN** el nivel se mantiene en `+2`
- **AND** la vista no cambia

### Requirement: Disminuir zoom
El atajo `Ctrl+-` DEBE disminuir el nivel de zoom en un paso.

#### Scenario: Disminuir desde el nivel por defecto
- **GIVEN** la vista visible en el nivel `0`
- **WHEN** el usuario presiona `Ctrl+-`
- **THEN** el nivel de zoom pasa a `-1`
- **AND** la interfaz se muestra más reducida (texto y elementos más pequeños)

#### Scenario: Disminuir en el mínimo
- **GIVEN** la vista visible en el nivel `-2`
- **WHEN** el usuario presiona `Ctrl+-`
- **THEN** el nivel se mantiene en `-2`
- **AND** la vista no cambia

### Requirement: Restablecer zoom
El atajo `Ctrl+0` DEBE restablecer el nivel de zoom al nivel por defecto `0`.

#### Scenario: Restablecer desde cualquier nivel
- **GIVEN** la vista visible en un nivel distinto de `0`
- **WHEN** el usuario presiona `Ctrl+0`
- **THEN** el nivel de zoom vuelve a `0`
- **AND** la interfaz se muestra en el tamaño por defecto

### Requirement: Redibujado al cambiar el zoom
El sistema DEBE redibujar la vista inmediatamente al cambiar el nivel de zoom, sin acumular contenido residual en pantalla.

#### Scenario: Cambio de nivel
- **GIVEN** la vista visible en un nivel de zoom cualquiera
- **WHEN** el usuario cambia el nivel con `Ctrl+=`, `Ctrl+-` o `Ctrl+0`
- **THEN** la vista se redibuja aplicando el nuevo nivel
- **AND** no queda contenido residual en pantalla

### Requirement: Aplicación global del zoom
El sistema DEBE aplicar el nivel de zoom de manera uniforme a toda la interfaz de la vista única de tareas.

#### Scenario: Zoom uniforme
- **GIVEN** la vista visible con un nivel de zoom distinto de `0`
- **WHEN** se muestra la vista
- **THEN** el nivel de zoom se aplica de forma consistente en toda la interfaz visible

### Requirement: Persistencia del nivel de zoom
El sistema DEBE persistir el nivel de zoom elegido y restaurarlo al iniciar la aplicación.

#### Scenario: Recordar el nivel entre sesiones
- **GIVEN** la vista visible con un nivel de zoom distinto de `0` (por ejemplo, `+2`)
- **WHEN** el usuario cierra la aplicación y la vuelve a iniciar
- **THEN** la vista se muestra en el nivel persistido (`+2`)

#### Scenario: Primer inicio sin nivel persistido
- **GIVEN** que no hay un nivel de zoom persistido
- **WHEN** se inicia la aplicación
- **THEN** la vista se muestra en el nivel por defecto `0`
