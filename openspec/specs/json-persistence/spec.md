# JSON Persistence

## Purpose
Define cómo persisten las tareas entre ejecuciones de la aplicación: se guardan en
un archivo JSON en el directorio de trabajo actual y se recargan al iniciar, de
modo que los datos no se pierdan al cerrar la terminal.

## Requirements

### Requirement: Carga inicial de tareas
El sistema DEBE cargar las tareas desde el archivo JSON al iniciar la aplicación.

#### Scenario: Archivo existente y válido
- **GIVEN** un archivo JSON existente con tareas válidas
- **WHEN** se inicia la aplicación
- **THEN** el sistema carga las tareas con su `id`, título y estado

#### Scenario: Archivo inexistente
- **GIVEN** que no existe el archivo JSON
- **WHEN** se inicia la aplicación
- **THEN** el sistema arranca con una lista de tareas vacía
- **AND** el archivo se crea recién al producirse el primer guardado

#### Scenario: Archivo corrupto o inválido
- **GIVEN** un archivo JSON existente pero que no se puede leer o no respeta el formato
- **WHEN** se inicia la aplicación
- **THEN** el sistema arranca con una lista de tareas vacía
- **AND** avisa al usuario de que el archivo no pudo cargarse

### Requirement: Persistencia tras cada operación
El sistema DEBE guardar las tareas en el archivo JSON inmediatamente después de
cada operación que modifique el estado de las tareas.

#### Scenario: Operación que modifica tareas
- **GIVEN** una operación de creación, completado, descompletado, eliminación o purga
- **WHEN** la operación se aplica con éxito
- **THEN** el archivo JSON se actualiza con el estado completo y actual de las tareas

#### Scenario: Operación de solo lectura
- **GIVEN** una operación que no modifica tareas (por ejemplo, listarlas)
- **WHEN** se ejecuta
- **THEN** el archivo JSON no se modifica

### Requirement: Formato del archivo
El sistema DEBE almacenar las tareas como un array JSON de objetos, donde cada
objeto representa una tarea con los campos `id`, `title` y `status`.

#### Scenario: Tareas guardadas
- **GIVEN** una o más tareas existentes
- **WHEN** se persiste el estado
- **THEN** el archivo contiene un array JSON
- **AND** cada elemento incluye los campos `id`, `title` y `status`

### Requirement: Escritura completa del estado
El sistema DEBE sobrescribir el archivo con el estado completo de las tareas en
cada guardado, reflejando fielmente las tareas en memoria.

#### Scenario: Guardado consistente
- **GIVEN** tareas en memoria tras una operación
- **WHEN** se persiste el estado
- **THEN** el archivo refleja exactamente el conjunto de tareas en memoria
- **AND** no persisten tareas eliminadas ni estados desactualizados

### Requirement: Generación de ids sin colisión
El sistema DEBE asignar a cada tarea nueva un id que no colisione con los ids
ya persistidos.

#### Scenario: Con tareas cargadas
- **GIVEN** tareas cargadas desde el archivo JSON
- **WHEN** se crea una tarea nueva
- **THEN** el id generado es mayor que el máximo id existente

#### Scenario: Sin tareas cargadas
- **GIVEN** que no hay tareas cargadas
- **WHEN** se crea la primera tarea
- **THEN** el sistema asigna un id inicial válido

### Requirement: Ubicación del archivo
El sistema DEBE utilizar un archivo JSON en el directorio de trabajo actual.

#### Scenario: Archivo de datos local
- **GIVEN** la aplicación en ejecución desde un directorio
- **WHEN** se persiste el estado
- **THEN** el archivo se guarda en el directorio de trabajo actual

### Requirement: Fallo de escritura
El sistema DEBE tolerar un fallo al escribir el archivo JSON sin perder las
tareas en memoria.

#### Scenario: Error al guardar
- **GIVEN** una operación que modifica tareas aplicada en memoria
- **WHEN** falla la escritura del archivo (por ejemplo, por permisos o disco lleno)
- **THEN** el sistema avisa al usuario del error
- **AND** las tareas se mantienen en memoria sin descartarse
