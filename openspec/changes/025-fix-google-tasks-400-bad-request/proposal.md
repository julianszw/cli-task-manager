# 025 — Fix Google Tasks 400 Bad Request

## Why

Al intentar sincronizar las tareas locales (crearlas o actualizarlas en Google Tasks), 
la API de Google devolvía el código de error genérico `400 Bad Request`.
Esto se debía a que el método `updateTask` estaba enviando el objeto `Task`
(cuerpo de la petición HTTP) sin el atributo `id` interno seteado, a pesar de 
que el ID sí iba en la URL. Además, la aplicación solo mostraba el mensaje 
"400 Bad Request" sin más detalles, dificultando la depuración del error.

## What Changes

- `HttpGoogleTasksClient.java`: 
  - Se modificó `updateTask` para agregar `.setId(taskId)` en la inicialización de la tarea.
  - Se implementó el método `handleException` para capturar `GoogleJsonResponseException` y extraer el mensaje de error específico que manda Google (detalles del error), exponiéndolo de forma clara en la UI si falla otra cosa.

## Impact

- **Fix**: Se pueden actualizar las tareas en Google Tasks sin error 400.
- **Mejora**: Ante fallos en la API de Google, la aplicación ahora muestra 
  exactamente qué falló según el backend de Google en lugar de un error HTTP genérico.
