# Google Tasks Sync

## Purpose
Define la sincronización bidireccional entre las tareas y listas locales (archivo
JSON) y Google Tasks —la API que respalda las tareas tachables que se muestran en
Google Calendar—, de modo que los cambios hechos en la app se reflejen en Google y
los cambios hechos en Google se reflejen en la app, sin reemplazar la persistencia
local.

## Requirements

### Requirement: Autenticación con Google
El sistema DEBE permitir autenticarse contra Google Tasks mediante el flujo OAuth
de app instalada (abriendo el navegador) y DEBE conservar las credenciales para no
volver a pedir autenticación en cada ejecución.

#### Scenario: Autenticación exitosa
- **GIVEN** un usuario que no está autenticado
- **WHEN** inicia la autenticación y autoriza el acceso en el navegador
- **THEN** el sistema obtiene acceso a Google Tasks
- **AND** conserva el token para reutilizarlo en ejecuciones futuras

#### Scenario: Sesión persistente
- **GIVEN** un token conservado de una sesión anterior
- **WHEN** se inicia la aplicación
- **THEN** el sistema reutiliza el token sin volver a pedir autenticación

#### Scenario: Autenticación cancelada
- **GIVEN** un usuario que cancela o deniega la autorización
- **WHEN** inicia la autenticación
- **THEN** la aplicación continúa funcionando en modo local
- **AND** no se produce ningún error

#### Scenario: Token expirado o revocado
- **GIVEN** un token que ya no es válido
- **WHEN** se intenta sincronizar
- **THEN** el sistema solicita autenticación nuevamente
- **AND** no pierde los datos locales

### Requirement: Vinculación de tareas y listas con Google
El sistema DEBE correlacionar cada tarea y cada lista local con su identificador
remoto de Google, conservando el id local numérico y almacenando por separado el id
remoto de texto.

#### Scenario: Tarea local nueva
- **GIVEN** una tarea local sin id remoto asociado
- **WHEN** se sincroniza
- **THEN** la tarea se crea en Google Tasks
- **AND** queda vinculada a su id remoto

#### Scenario: Tarea remota nueva
- **GIVEN** una tarea existente en Google Tasks sin correspondencia local
- **WHEN** se sincroniza
- **THEN** la tarea se crea localmente
- **AND** queda vinculada a su id remoto

#### Scenario: Lista local nueva
- **GIVEN** una lista local sin id remoto asociado
- **WHEN** se sincroniza
- **THEN** la lista se crea en Google Tasks
- **AND** queda vinculada a su id remoto

#### Scenario: Lista remota nueva
- **GIVEN** una lista existente en Google Tasks sin correspondencia local
- **WHEN** se sincroniza
- **THEN** la lista se crea localmente
- **AND** queda vinculada a su id remoto

### Requirement: Sincronización de título, estado y lista
El sistema DEBE sincronizar entre el lado local y Google Tasks el título, el estado
y la lista de pertenencia de cada tarea.

#### Scenario: Mapeo de estado
- **GIVEN** una tarea en estado `PENDING`
- **WHEN** se sincroniza con Google Tasks
- **THEN** la tarea queda sin tachar en Google (equivalente a `needsAction`)

#### Scenario: Mapeo de estado completado
- **GIVEN** una tarea en estado `COMPLETED`
- **WHEN** se sincroniza con Google Tasks
- **THEN** la tarea queda tachada en Google (equivalente a `completed`)

### Requirement: Subir cambios locales
El sistema DEBE reflejar en Google Tasks los cambios locales realizados desde la
última sincronización.

#### Scenario: Cambios locales pendientes
- **GIVEN** cambios locales pendientes (crear, completar, reabrir, renombrar, mover
  o eliminar tareas; o crear listas)
- **WHEN** se sincroniza
- **THEN** esos cambios se aplican en Google Tasks

#### Scenario: Sin cambios locales
- **GIVEN** que no hay cambios locales desde la última sincronización
- **WHEN** se sincroniza
- **THEN** no se envía ninguna modificación a Google Tasks

### Requirement: Bajar cambios remotos
El sistema DEBE reflejar localmente los cambios realizados en Google Tasks desde la
última sincronización.

#### Scenario: Cambios remotos pendientes
- **GIVEN** cambios realizados en Google Tasks (crear, completar, reabrir, renombrar,
  mover o eliminar tareas; o crear listas)
- **WHEN** se sincroniza
- **THEN** esos cambios se aplican localmente
- **AND** se persisten en el archivo JSON local

#### Scenario: Sin cambios remotos
- **GIVEN** que no hay cambios en Google Tasks desde la última sincronización
- **WHEN** se sincroniza
- **THEN** no se aplica ninguna modificación local

### Requirement: Eliminación sin resurrección
El sistema DEBE recordar las tareas y listas eliminadas para no volver a
recrearlas desde el lado contrario en sincronizaciones posteriores.

#### Scenario: Tarea eliminada localmente
- **GIVEN** una tarea eliminada localmente que aún existe en Google Tasks
- **WHEN** se sincroniza
- **THEN** la tarea se elimina en Google Tasks
- **AND** no se vuelve a crear localmente en sincronizaciones posteriores

#### Scenario: Tarea eliminada en Google
- **GIVEN** una tarea eliminada en Google Tasks que aún existe localmente
- **WHEN** se sincroniza
- **THEN** la tarea se elimina localmente
- **AND** no se vuelve a crear en Google en sincronizaciones posteriores

### Requirement: Resolución de conflictos
El sistema DEBE resolver los conflictos aplicando la versión modificada más
recientemente.

#### Scenario: Conflicto en una tarea
- **GIVEN** una tarea modificada tanto localmente como en Google Tasks desde la
  última sincronización
- **WHEN** se sincroniza
- **THEN** prevalece la versión con la modificación más reciente

#### Scenario: Cambio en un solo lado
- **GIVEN** una tarea modificada solo localmente o solo en Google Tasks
- **WHEN** se sincroniza
- **THEN** prevalece el lado modificado sin conflicto

### Requirement: Sincronización al iniciar
El sistema DEBE sincronizar automáticamente al iniciar la aplicación cuando el
usuario está autenticado.

#### Scenario: Inicio con usuario autenticado
- **GIVEN** un usuario autenticado
- **WHEN** se inicia la aplicación
- **THEN** el sistema sincroniza con Google Tasks antes de que el usuario interactúe

#### Scenario: Inicio sin autenticación
- **GIVEN** un usuario no autenticado
- **WHEN** se inicia la aplicación
- **THEN** el sistema arranca en modo local sin intentar sincronizar

### Requirement: Sincronización manual
El sistema DEBE permitir disparar la sincronización manualmente mediante un atajo
de teclado.

#### Scenario: Sincronización manual
- **GIVEN** la aplicación en ejecución
- **WHEN** el usuario activa el atajo de sincronización
- **THEN** el sistema sincroniza con Google Tasks
- **AND** muestra el resultado al usuario

### Requirement: Manejo de errores de red
El sistema DEBE tolerar los fallos de conexión o de la API de Google sin perder los
datos locales y avisando al usuario.

#### Scenario: Fallo durante la sincronización
- **GIVEN** un fallo de red o de la API durante la sincronización
- **WHEN** se intenta sincronizar
- **THEN** el sistema avisa al usuario del error
- **AND** los datos locales permanecen intactos

### Requirement: Persistencia local intacta
El sistema DEBE seguir utilizando el archivo JSON local como fuente de los datos en
la aplicación, siendo la sincronización con Google Tasks un mecanismo adicional y no
un reemplazo de la persistencia local.

#### Scenario: Modo local sin conexión
- **GIVEN** la aplicación sin acceso a Google Tasks
- **WHEN** se realizan operaciones locales (crear, completar, renombrar, mover,
  eliminar)
- **THEN** los cambios se guardan en el archivo JSON local
- **AND** quedan pendientes de sincronizar para cuando haya conexión
