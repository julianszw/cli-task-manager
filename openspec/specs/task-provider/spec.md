# Task Provider

## Purpose
Define que la aplicación opera exclusivamente contra una API externa de tareas —
Google Tasks como primer proveedor —, convirtiéndose en una interfaz sobre sus
métodos, sin persistencia local (ni archivo JSON ni repositorio en memoria
persistente). Define además una abstracción de proveedor que deja la puerta abierta
a integrar otros proveedores (por ejemplo, Microsoft TO DO), y el modelo de datos de
Google Tasks, que incluye la fecha de vencimiento.

## Requirements

### Requirement: Fuente de datos externa única
El sistema DEBE usar la Google Tasks API como única fuente de datos, eliminando la
persistencia local en archivo JSON y el repositorio en memoria persistente.

#### Scenario: Sin autenticación
- **GIVEN** un usuario no autenticado
- **WHEN** se inicia la aplicación
- **THEN** el sistema solicita autenticación
- **AND** no puede operar sobre listas ni tareas hasta completarla

#### Scenario: Sin conexión
- **GIVEN** la aplicación sin conexión a la Google Tasks API
- **WHEN** se intenta cualquier operación sobre listas o tareas
- **THEN** el sistema avisa del error
- **AND** no existe modo offline ni datos locales sobre los que operar

### Requirement: Abstracción de proveedor de tareas
El sistema DEBE definir una abstracción de proveedor con las operaciones sobre
listas y tareas, de modo que Google Tasks sea una implementación reemplazable por
otros proveedores (por ejemplo, Microsoft TO DO) sin cambiar el resto de la
aplicación.

#### Scenario: Proveedor intercambiable
- **GIVEN** la aplicación construida sobre la abstracción de proveedor
- **WHEN** se integra un nuevo proveedor (por ejemplo, Microsoft TO DO)
- **THEN** el proveedor se incorpora implementando la misma abstracción
- **AND** las capas superiores (servicio y CLI) no cambian

### Requirement: Proveedor Google Tasks
El sistema DEBE implementar la abstracción de proveedor con la Google Tasks API
(https://developers.google.com/workspace/tasks/reference/rest).

#### Scenario: Uso de la API
- **GIVEN** el proveedor Google Tasks en uso
- **WHEN** se ejecuta una operación sobre listas o tareas
- **THEN** la operación se resuelve llamando al método correspondiente de la
  Google Tasks API

### Requirement: Autenticación con Google
El sistema DEBE autenticarse contra Google Tasks mediante el flujo OAuth de app
instalada (abriendo el navegador) y DEBE conservar las credenciales para no volver
a pedir autenticación en cada ejecución.

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
- **THEN** el sistema no obtiene acceso a Google Tasks
- **AND** la aplicación no puede operar hasta autenticarse

#### Scenario: Token expirado o revocado
- **GIVEN** un token que ya no es válido
- **WHEN** se intenta una operación
- **THEN** el sistema solicita autenticación nuevamente

### Requirement: Operaciones sobre listas de tareas
El sistema DEBE soportar, sobre las listas de tareas (tasklists), listar, obtener,
crear, actualizar (renombrar) y eliminar.

#### Scenario: Listar listas
- **GIVEN** el usuario autenticado
- **WHEN** se solicitan las listas
- **THEN** el sistema devuelve todas las listas de tareas del usuario en Google Tasks

#### Scenario: Obtener una lista
- **GIVEN** el id de una lista existente
- **WHEN** se solicita la lista
- **THEN** el sistema devuelve esa lista con su id y título

#### Scenario: Crear lista
- **GIVEN** un título de lista no vacío
- **WHEN** se solicita crear la lista
- **THEN** la lista se crea en Google Tasks
- **AND** el sistema devuelve la lista con el id asignado por Google

#### Scenario: Renombrar lista
- **GIVEN** una lista existente y un título nuevo no vacío
- **WHEN** se solicita actualizar la lista
- **THEN** el título de la lista cambia en Google Tasks

#### Scenario: Eliminar lista
- **GIVEN** el id de una lista existente
- **WHEN** se solicita eliminar la lista
- **THEN** la lista se elimina en Google Tasks

### Requirement: Operaciones sobre tareas
El sistema DEBE soportar, sobre las tareas de una lista, listar, obtener, crear,
actualizar, eliminar, mover y limpiar (clear, que elimina todas las tareas de la
lista).

#### Scenario: Listar tareas de una lista
- **GIVEN** el id de una lista existente
- **WHEN** se solicitan las tareas de esa lista
- **THEN** el sistema devuelve todas las tareas de esa lista

#### Scenario: Obtener una tarea
- **GIVEN** el id de una tarea existente y su lista
- **WHEN** se solicita la tarea
- **THEN** el sistema devuelve esa tarea con sus datos

#### Scenario: Crear tarea
- **GIVEN** el id de una lista existente y un título no vacío
- **WHEN** se solicita crear la tarea
- **THEN** la tarea se crea en Google Tasks dentro de esa lista
- **AND** el sistema devuelve la tarea con el id asignado por Google

#### Scenario: Actualizar tarea
- **GIVEN** una tarea existente
- **WHEN** se solicita actualizar su título, estado o fecha
- **THEN** los cambios se aplican en Google Tasks

#### Scenario: Eliminar tarea
- **GIVEN** el id de una tarea existente
- **WHEN** se solicita eliminar la tarea
- **THEN** la tarea se elimina en Google Tasks

#### Scenario: Mover tarea a otra lista
- **GIVEN** una tarea existente y una lista destino distinta de la actual
- **WHEN** se solicita mover la tarea
- **THEN** la tarea queda asociada a la lista destino en Google Tasks

#### Scenario: Limpiar lista
- **GIVEN** el id de una lista existente
- **WHEN** se solicita limpiar la lista
- **THEN** se eliminan todas las tareas de esa lista en Google Tasks

### Requirement: Modelo de datos de Google Tasks
El sistema DEBE modelar cada lista y cada tarea según el modelo de Google Tasks,
usando el identificador de texto de Google como id, sin ids locales numéricos.

#### Scenario: Modelo de lista
- **GIVEN** una lista de tareas
- **WHEN** se representa en el sistema
- **THEN** la lista expone su `id` (texto) y su `title`

#### Scenario: Modelo de tarea
- **GIVEN** una tarea
- **WHEN** se representa en el sistema
- **THEN** la tarea expone su `id` (texto), `title`, estado, `listId` y, opcionalmente,
  su fecha de vencimiento (`due`)

### Requirement: Mapeo de estado
El sistema DEBE mapear el estado de cada tarea entre el modelo de la aplicación y el
de Google Tasks: `PENDING` equivale a `needsAction` y `COMPLETED` equivale a
`completed`.

#### Scenario: Tarea pendiente
- **GIVEN** una tarea en estado `PENDING`
- **WHEN** se comunica con Google Tasks
- **THEN** la tarea queda con estado `needsAction`

#### Scenario: Tarea completada
- **GIVEN** una tarea en estado `COMPLETED`
- **WHEN** se comunica con Google Tasks
- **THEN** la tarea queda con estado `completed`

### Requirement: Fecha de vencimiento
El sistema DEBE modelar y permitir editar la fecha de vencimiento (`due`) de una
tarea, mapeándola al campo `due` de Google Tasks (RFC 3339, opcional). Al enviar la
fecha a Google Tasks, el sistema DEBE convertir la fecha en formato `yyyy-MM-dd` a
un timestamp RFC 3339 completo (con componente de hora).

#### Scenario: Tarea con fecha de vencimiento
- **GIVEN** una tarea con fecha de vencimiento en Google Tasks
- **WHEN** se obtiene la tarea
- **THEN** el sistema expone esa fecha como atributo de la tarea

#### Scenario: Tarea sin fecha de vencimiento
- **GIVEN** una tarea sin fecha de vencimiento
- **WHEN** se obtiene la tarea
- **THEN** el atributo de fecha queda vacío

#### Scenario: Establecer o actualizar fecha
- **GIVEN** una tarea existente y una fecha de vencimiento
- **WHEN** se solicita establecer o modificar la fecha
- **THEN** la fecha se actualiza en Google Tasks

#### Scenario: Fecha ingresada como fecha-sola
- **GIVEN** una fecha de vencimiento ingresada en formato `yyyy-MM-dd` (por ejemplo, `2026-08-29`)
- **WHEN** se crea o actualiza una tarea con esa fecha
- **THEN** el sistema envía la fecha a Google Tasks como un timestamp RFC 3339 completo (con componente de hora)
- **AND** Google Tasks acepta la fecha sin devolver un error de argumento inválido

#### Scenario: Quitar fecha
- **GIVEN** una tarea con fecha de vencimiento
- **WHEN** se solicita quitar la fecha
- **THEN** la tarea queda sin fecha de vencimiento en Google Tasks

### Requirement: Descripción fuera de alcance
El sistema NO DEBE modelar ni mostrar la descripción (`notes`) de las tareas por
ahora.

#### Scenario: Tarea con descripción
- **GIVEN** una tarea que tiene descripción en Google Tasks
- **WHEN** se opera con la tarea en la aplicación
- **THEN** la descripción no se modela ni se muestra
- **AND** la descripción original en Google Tasks no se ve afectada por el resto de
  operaciones

### Requirement: Manejo de errores de la API
El sistema DEBE tolerar los fallos de la Google Tasks API avisando al usuario, sin
corromper el estado mostrado.

#### Scenario: Fallo durante una operación
- **GIVEN** un fallo de red o de la API durante una operación
- **WHEN** se intenta la operación
- **THEN** el sistema avisa al usuario del error
- **AND** el estado mostrado en la aplicación permanece consistente
