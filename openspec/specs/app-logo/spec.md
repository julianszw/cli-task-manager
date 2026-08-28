# App Logo

## Purpose
Define el logo de la aplicación mostrado en la parte superior de la vista: el texto "TaskMaster" dibujado en grande con caracteres de bloque (estilo FIGlet), en el color de acento definido en `visual-style`, que permanece visible como cabecera por encima de la lista de tareas. Define además el nombre de la aplicación mostrado como título de la ventana de la terminal.

## Requirements

### Requirement: Logo ASCII "TaskMaster"
El sistema DEBE mostrar en la parte superior de la vista un logo hecho con caracteres que represente el texto "TaskMaster" en letras grandes.

#### Scenario: Logo visible al iniciar
- **GIVEN** la aplicación iniciada
- **WHEN** se muestra la vista
- **THEN** se muestra el logo "TaskMaster" en letras grandes en la parte superior
- **AND** el logo se dibuja con caracteres, sin recursos gráficos externos

### Requirement: Cabecera superior fija
El logo DEBE ubicarse arriba de todo, por encima de la lista de tareas, permaneciendo visible como cabecera.

#### Scenario: Cabecera fija
- **GIVEN** la vista visible
- **WHEN** se muestra la vista
- **THEN** el logo se muestra en la parte superior
- **AND** la lista de tareas aparece debajo del logo
- **AND** la barra de estado permanece en el borde inferior (según `visual-style`)

### Requirement: Color del logo
El logo DEBE mostrarse en el color de acento definido en `visual-style`.

#### Scenario: Color de acento
- **GIVEN** la vista visible
- **WHEN** se muestra el logo
- **THEN** el logo se muestra en el color de acento (cian)

### Requirement: Adaptación al ancho del terminal
El logo DEBE adaptarse al ancho del terminal sin romper el resto de la vista.

#### Scenario: Terminal angosto
- **GIVEN** un ancho de terminal que no alcanza para el logo completo
- **WHEN** se muestra la vista
- **THEN** el logo se trunca o se omite sin desbordar la vista
- **AND** el resto de la vista (lista y barra de estado) sigue mostrándose correctamente

### Requirement: Título de la ventana de la terminal
El sistema DEBE mostrar el nombre de la aplicación, "TaskMaster", como título de la
ventana de la terminal, en lugar del título por defecto del frame de Swing.

#### Scenario: Título visible
- **GIVEN** la aplicación iniciada
- **WHEN** se muestra la ventana de la terminal
- **THEN** el título de la ventana muestra "TaskMaster"
