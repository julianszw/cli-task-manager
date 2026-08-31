# Tareas — 032 code review hardening

## Performance (arranque)
- [x] #1 Cachear `Tasks`/transporte en `GoogleTasksProvider` (constructor único)
- [x] #2 Cachear flow/transporte en `GoogleAuth` (init lazy) y no silenciar errores en `hasStoredCredentials()`
- [x] #3 Carga de tareas: solo cacheo, sin concurrencia (cubierto por #1)

## Dead code / contrato
- [x] #5 Recortar `TaskProvider` y sus implementaciones (`GoogleTasksProvider`, `FakeTaskProvider`)
- [x] #12 Quitar `APPLICATION_NAME` sin uso en `GoogleAuth`

## Modelo de dominio
- [x] #9 Fecha de vencimiento como `LocalDate` en `Task`, `TaskService`, `TaskProvider` y conversión aislada en `GoogleTasksProvider`

## Clean code
- [x] #4 Dividir `TaskListWindow.handleInput()` en handlers pequeños
- [x] #6 Extraer `MenuNavigation.cycle` y reutilizarlo en `TaskActionMenuWindow` y `OptionMenuWindow`
- [x] #7 Centralizar `try/catch` con helper `call(...)` en `GoogleTasksProvider`

## Correcciones
- [x] #8 Paginar listas y tareas hasta agotar `pageToken` en `GoogleTasksProvider`

## Arranque / build
- [x] #13 Separar composition root en `App.run()` y mostrar errores legibles sin stack trace
- [x] #17 Fijar `maven-compiler-plugin` (release 21) y añadir `maven-enforcer-plugin`

## Documentación
- [x] #10 Unificar nombre de producto a "TaskMaster" (`README.md`, `project.md`)
- [x] #11 Añadir teclas `h` y `d` al README y ajustar la descripción de la vista
- [x] #15 Ajustar "sin frameworks externos" en `project.md`
- [x] #16 Corregir resumen de zoom en `REGISTER.md`
- [x] #14 Revisar helpers solo-test de `AppLogo` (opcional: se conserva como API de paquete de apoyo a tests)

## Verificación
- [x] `mvn test` (actualizar tests afectados por #5/#9)
- [x] `mvn package`
