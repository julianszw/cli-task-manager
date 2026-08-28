# Visual Style

## Purpose
Define el lenguaje visual de la interfaz, inspirado en agentty: un único tema oscuro, bordes redondeados, separadores tenues, una barra de estado inferior de altura fija con franjas de acento y la presentación de tareas como lista con íconos de estado. Aplica a toda la vista única definida en `interactive-cli` y reemplaza a la capacidad de alternancia de tema (`dark-theme`).

## Requirements

### Requirement: Tema oscuro único
El sistema DEBE mostrar siempre un tema oscuro, sin posibilidad de alternar a un tema claro.

#### Scenario: Inicio con tema oscuro
- **GIVEN** la aplicación iniciada
- **WHEN** se muestra la vista
- **THEN** el fondo es oscuro y el texto claro
- **AND** no existe ninguna tecla ni acción para alternar a un tema claro

### Requirement: Paleta de colores
El sistema DEBE aplicar una paleta de colores consistente en toda la vista: texto blanco brillante sobre fondo oscuro, acento en cian, y colores semánticos verde (ícono de completado), gris (texto de tareas completadas), amarillo (advertencia) y rojo (error).

#### Scenario: Texto y fondo
- **GIVEN** la vista visible
- **WHEN** se muestra cualquier texto
- **THEN** el texto principal se muestra en blanco brillante sobre fondo oscuro

#### Scenario: Texto secundario
- **GIVEN** la vista visible
- **WHEN** se muestra texto secundario (etiquetas, atajos, metadatos)
- **THEN** el texto secundario se muestra atenuado

#### Scenario: Colores semánticos
- **GIVEN** la vista visible
- **WHEN** se muestra una tarea completada, un aviso o un error
- **THEN** se usa verde para el ícono de completado, gris para el texto de tareas completadas, amarillo para avisos y rojo para errores

### Requirement: Bordes redondeados
El sistema DEBE dibujar los paneles y cajas con bordes redondeados usando caracteres de dibujo de caja (`╭` `─` `╮` `│` `╰` `╯`), en lugar de bordes rectos.

#### Scenario: Panel con borde redondeado
- **GIVEN** la vista visible
- **WHEN** se muestra un panel o caja (por ejemplo, la lista de tareas o el campo de entrada)
- **THEN** el panel se enmarca con `╭` en la esquina superior izquierda, `╮` en la superior derecha, `│` en los laterales y `╰` `╯` en las esquinas inferiores

### Requirement: Separadores tenues
El sistema DEBE separar secciones de la vista con líneas horizontales tenues (`───`).

#### Scenario: Secciones separadas
- **GIVEN** la vista con varias secciones (lista, ayuda, mensajes)
- **WHEN** se muestra la vista
- **THEN** las secciones se separan con líneas `───` atenuadas

### Requirement: Barra de estado inferior
El sistema DEBE mostrar una barra de estado en el borde inferior de la vista, de altura fija, que no cambie de tamaño al aparecer o desaparecer mensajes.

#### Scenario: Barra siempre presente
- **GIVEN** la vista visible
- **WHEN** se muestra la vista
- **THEN** la barra de estado se muestra en el borde inferior
- **AND** su altura es fija

#### Scenario: Sin desplazamiento al mostrar mensajes
- **GIVEN** la vista con la barra de estado visible
- **WHEN** aparece o desaparece un mensaje
- **THEN** la altura de la barra no cambia
- **AND** el resto de la vista no se desplaza verticalmente

### Requirement: Franjas de acento
El sistema DEBE mostrar franjas horizontales tenues en el color de acento en los bordes superior e inferior de la barra de estado (`▔▔▔` arriba y `▁▁▁` abajo).

#### Scenario: Franjas de acento visibles
- **GIVEN** la barra de estado visible
- **WHEN** se muestra la barra
- **THEN** una franja `▔▔▔` se muestra en el borde superior y una franja `▁▁▁` en el borde inferior
- **AND** ambas franjas se muestran en el color de acento, atenuadas

### Requirement: Chip de título
El sistema DEBE mostrar el título de la aplicación en la barra de estado con un borde izquierdo de acento (`▎`) y el texto en negrita, truncado al centro si excede el ancho disponible.

#### Scenario: Título visible
- **GIVEN** la barra de estado visible
- **WHEN** se muestra la barra
- **THEN** el título se muestra precedido por el borde `▎` en el color de acento
- **AND** el texto del título se muestra en negrita

#### Scenario: Título truncado
- **GIVEN** un ancho de terminal reducido
- **WHEN** el título excede el espacio disponible
- **THEN** el título se trunca por el centro, manteniendo el inicio y el final

### Requirement: Contador de tareas
El sistema DEBE mostrar en la barra de estado un resumen con la cantidad de tareas pendientes y completadas.

#### Scenario: Contador visible
- **GIVEN** la vista con tareas
- **WHEN** se muestra la barra de estado
- **THEN** se muestra la cantidad de tareas pendientes y de tareas completadas

### Requirement: Banner de mensajes
El sistema DEBE mostrar los mensajes y avisos en una línea fija de la barra de estado, sin alterar el tamaño de la vista.

#### Scenario: Mensaje informativo
- **GIVEN** un mensaje informativo (por ejemplo, "no hay tareas completadas")
- **WHEN** el mensaje debe mostrarse
- **THEN** el mensaje aparece en la línea de banner de la barra de estado

#### Scenario: Aviso
- **GIVEN** un aviso (por ejemplo, un archivo de tareas no legible)
- **WHEN** el aviso debe mostrarse
- **THEN** el aviso aparece en la línea de banner en color amarillo

### Requirement: Fila de atajos adaptativa
El sistema DEBE mostrar los atajos de teclado en la barra de estado con la tecla en negrita y la etiqueta atenuada, adaptándose al ancho del terminal.

#### Scenario: Atajos con tecla destacada
- **GIVEN** la barra de estado visible
- **WHEN** se muestran los atajos
- **THEN** cada atajo muestra su tecla en negrita y su etiqueta atenuada

#### Scenario: Adaptación al ancho
- **GIVEN** un ancho de terminal que no alcanza para todos los atajos
- **WHEN** se muestra la fila de atajos
- **THEN** se omiten primero las etiquetas y luego los atajos de menor prioridad
- **AND** se conserva siempre al menos el último atajo

### Requirement: Tipografía con small-caps y atenuado
El sistema DEBE usar mayúsculas espaciadas (small-caps) para las etiquetas de sección y atenuar el texto secundario.

#### Scenario: Etiquetas de sección
- **GIVEN** la vista con etiquetas de sección
- **WHEN** se muestra una etiqueta de sección
- **THEN** la etiqueta se muestra en mayúsculas con letras espaciadas

### Requirement: Resaltado de la selección
El sistema DEBE resaltar la tarea seleccionada con un borde izquierdo de acento (`▎`) y texto en negrita, distinguiéndola de las demás.

#### Scenario: Selección resaltada
- **GIVEN** una tarea seleccionada
- **WHEN** se muestra la lista
- **THEN** la tarea seleccionada se muestra con `▎` a la izquierda y su título en negrita
- **AND** las tareas no seleccionadas no muestran ese borde
