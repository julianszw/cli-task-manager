# Code Review — mejora de código, performance, buenas prácticas, dead code y documentación

## Resumen

La arquitectura en capas (`cli` → `service` → `provider` → `model`) es sólida y la
separación de responsabilidades es correcta en líneas generales. Los problemas más
importantes no son de estructura sino de **eficiencia en el arranque** (el cliente de
Google Tasks y el transporte HTTP se reconstruyen en cada llamada) y de **higiene de
código** (métodos monolíticos, duplicación y API muerta en `TaskProvider`). No hay
hallazgos críticos (sin pérdida de datos ni vulnerabilidades). Conteo: **2 Alto**,
**9 Medio**, **6 Bajo**.

---

## Hallazgos

### 1. [Alto] El cliente de Google Tasks y el transporte HTTP se reconstruyen en cada llamada
- Archivo: `src/main/java/tasktracker/google/GoogleTasksProvider.java:162-173`
- Problema: `service()` crea un `Tasks.Builder` **y** un nuevo
  `GoogleNetHttpTransport.newTrustedTransport()` en *cada* operación. `newTrustedTransport()`
  inicializa el contexto SSL/socket factory, que es caro. Durante `load()` se invoca
  `service()` una vez para listas y una vez por cada lista (`listTasks`), de modo que el
  arranque paga ese costo N+1 veces; además toda operación interactiva lo repaga.
- Principio: Clean Code — no repetir trabajo costoso; Performance — cachear recursos caros.
- Cambio sugerido: construir el `Tasks` (y el `Credential`) una sola vez y reutilizarlo.

```java
public final class GoogleTasksProvider implements TaskProvider {
    private final Tasks tasks; // construido una vez en el constructor

    public GoogleTasksProvider(GoogleAuth auth) {
        this.tasks = new Tasks.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                auth.loadCredential())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Tasks service() {
        return tasks; // o directamente usar tasks en cada método
    }
}
```

### 2. [Alto] `GoogleAuth` reconstruye flow/transporte en cada uso y enmascara errores
- Archivo: `src/main/java/tasktracker/google/GoogleAuth.java:32-46, 71-80`
- Problema: `hasStoredCredentials()`, `loadCredential()` y `authorize()` llaman cada una a
  `buildFlow()`, que vuelve a crear `NetHttpTransport` + `FileDataStoreFactory`. En el
  arranque (`App.main`) se construye el flujo dos veces. Además
  `hasStoredCredentials()` captura `Exception` y devuelve `false` **silenciosamente**, lo
  que oculta errores reales (p. ej. `credentials.json` ausente) y deriva en un fallo
  confuso más tarde.
- Principio: Clean Code — no usar excepciones como flujo de control; Performance — cachear
  transporte/credencial.
- Cambio sugerido: cachear `Credential` y `flow`/`transport` (lazy, una sola vez) y no
  tragar excepciones indiscriminadamente.

```java
private volatile GoogleAuthorizationCodeFlow flow;

private GoogleAuthorizationCodeFlow flow() throws IOException, GeneralSecurityException {
    GoogleAuthorizationCodeFlow f = flow;
    if (f == null) {
        synchronized (this) {
            if (flow == null) flow = buildFlow();
            f = flow;
        }
    }
    return f;
}

public boolean hasStoredCredentials() {
    try {
        return flow().loadCredential("user") != null;
    } catch (IOException | GeneralSecurityException e) {
        throw new ProviderException("No se pudo verificar la sesión guardada: " + e.getMessage(), e);
    }
}
```

### 3. [Medio] `TaskService.load()` hace N+1 llamadas de red secuenciales
- Archivo: `src/main/java/tasktracker/service/TaskService.java:30-39`
- Problema: por cada lista hace una llamada `listTasks` adicional (una por lista). Con
  muchas listas el arranque se degrada linealmente y de forma secuencial.
- Principio: Performance — evitar N+1; aprovechar concurrencia cuando el backend lo permite.
- Cambio sugerido: cargar las tareas de cada lista en paralelo (la Google Tasks API no
  ofrece "todas las tareas de todas las listas"), con un `ExecutorService` o streams
  paralelos acotados, y luego poblar los mapas.

```java
public void load() {
    lists.clear();
    tasks.clear();
    List<TaskList> allLists = provider.listTaskLists();
    allLists.forEach(list -> lists.put(list.getId(), list));

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        allLists.stream()
            .<Callable<List<Task>>>map(list -> () -> provider.listTasks(list.getId()))
            .toList().stream()
            .flatMap(c -> { try { return executor.submit(c).get(); } catch (...) {...} })
            .forEach(task -> tasks.put(task.getId(), task));
    }
}
```
> Nota: si se prefiere simplicidad sobre concurrencia, al menos la caché del hallazgo #1
> elimina la mayor parte del costo.

### 4. [Medio] `TaskListWindow.handleInput()` es un método monolítico
- Archivo: `src/main/java/tasktracker/cli/TaskListWindow.java:125-217`
- Problema: ~90 líneas que mezclan zoom, navegación de lista, acciones de tarea y salida,
  con dos `switch` casi idénticos (Ctrl+carácter y carácter plano) y una cadena de
  `else if`. Difícil de leer, extender y testear.
- Principio: Clean Code — una función hace una sola cosa; un nivel de abstracción.
- Cambio sugerido: dividir en handlers pequeños y un despachador declarativo.

```java
@Override
public boolean handleInput(KeyStroke key) {
    if (key.isCtrlDown()) {
        return handleZoom(key);        // Ctrl+=, Ctrl+-, Ctrl+0
    }
    return switch (key.getKeyType()) {
        case Character -> handleChar(key.getCharacter());
        case Tab -> { nextList(); yield true; }
        case ReverseTab -> { previousList(); yield true; }
        case Enter -> { openActionMenu(); yield true; }
        case ArrowUp -> { moveUp(); yield true; }
        case ArrowDown -> { moveDown(); yield true; }
        case Escape -> { requestExit(); yield true; }
        default -> super.handleInput(key);
    };
}
```

### 5. [Medio] API muerta en `TaskProvider` (interfaz sobredimensionada)
- Archivo: `src/main/java/tasktracker/provider/TaskProvider.java:11-31`
- Problema: `getTaskList`, `updateTaskList`, `deleteTaskList`, `getTask` y `clearTasks`
  no se usan en producción (solo se implementan en `GoogleTasksProvider` y
  `FakeTaskProvider`). El proyecto ya tuvo dos limpiezas de dead code (changes `013` y
  `015`); esta superficie volvió a crecer.
- Principio: Clean Code — eliminar código en desuso; YAGNI.
- Cambio sugerido: recortar la interfaz a lo que usa `TaskService`
  (`listTaskLists`, `createTaskList`, `listTasks`, `createTask`, `updateTask`,
  `deleteTask`, `moveTask`, `providerName`) y eliminar los métodos correspondientes en
  `GoogleTasksProvider` y `FakeTaskProvider`. `clearTasks` no es necesario: el purge ya
  usa `deleteTask`.

### 6. [Medio] Navegación de menú duplicada entre `TaskActionMenuWindow` y `OptionMenuWindow`
- Archivo: `src/main/java/tasktracker/cli/TaskActionMenuWindow.java:61-102` y
  `src/main/java/tasktracker/cli/OptionMenuWindow.java:46-87`
- Problema: `moveSelection` y `handleInput` (teclas `k`/`j`, flechas, `Esc`, aritmética
  modular para ciclar) están copiados casi idénticos en dos clases.
- Principio: Clean Code — DRY (no repetir).
- Cambio sugerido: extraer una clase base común (p. ej. `SelectableListWindow`) o un
  helper estático `MenuNavigation.cycle(int selected, int delta, int count)`.

### 7. [Medio] Boilerplate `try/catch` repetido en `GoogleTasksProvider`
- Archivo: `src/main/java/tasktracker/google/GoogleTasksProvider.java` (todas las operaciones)
- Problema: cada método repite `try { ... } catch (IOException e) { throw handleException(...) }`.
  Ruido que oculta la intención real de cada operación.
- Principio: Clean Code — una función hace una sola cosa; eliminar duplicación.
- Cambio sugerido: centralizar el manejo con un helper funcional.

```java
private <T> T call(String message, Supplier<T> operation) {
    try {
        return operation.get();
    } catch (IOException e) {
        throw handleException(message, e);
    }
}

@Override
public List<TaskList> listTaskLists() {
    return call("No se pudieron listar las listas", () -> {
        var items = tasks.tasklists().list().setMaxResults(100).execute().getItems();
        return items == null ? List.of() : items.stream().map(GoogleTasksProvider::toTaskList).toList();
    });
}
```

### 8. [Medio] Límite de paginación en 100 sin continuar con `pageToken`
- Archivo: `src/main/java/tasktracker/google/GoogleTasksProvider.java:28, 84`
- Problema: `setMaxResults(100)` sin iterar `pageToken` descarta silenciosamente listas y
  tareas más allá de las primeras 100. Es un bug de corrección (no de estilo): datos que
  no se muestran.
- Principio: Correctness / Clean Code — no truncar datos en silencio.
- Cambio sugerido: paginar con `setPageToken(next)` hasta agotar `getNextPageToken()`, o
  al menos documentar la limitación si se acepta de forma deliberada.

### 9. [Medio] Modelo de dominio anémico y fechas "stringly-typed"
- Archivo: `src/main/java/tasktracker/model/Task.java:3-71` y
  `src/main/java/tasktracker/service/TaskService.java:155-166`
- Problema: `Task`/`TaskList` son mutables con getters/setters; la fecha de vencimiento es
  un `String` con sufijo mágico `T00:00:00.000Z` (asume UTC) y `getDueDate()` lo
  re-parsea en cada render con `indexOf('T')`, acoplando el modelo al formato de Google.
- Principio: Clean Architecture — modelo de dominio rico; no exponer detalles de
  infraestructura en el dominio.
- Cambio sugerido: modelar `due` como `LocalDate` (o un value object `DueDate`) y convertir
  a/desde el formato de Google solo en el adaptador `GoogleTasksProvider`. Extraer el
  formato de fecha a una constante/conversor único.

```java
// model/Task.java (esbozo)
private LocalDate due;
public LocalDate getDue() { return due; }
```

### 10. [Medio] Inconsistencia de marca/logo entre código y documentación
- Archivo: `src/main/java/tasktracker/cli/AppLogo.java:7` (`TASKMASTER`),
  `src/main/java/tasktracker/cli/TaskViewRenderer.java:12` (`TaskMaster`),
  `README.md:24-25` y `openspec/project.md:20` (ambos dicen logo "Task Manager").
- Problema: el nombre del producto difiere en logo ("TASKMASTER"), título de ventana
  ("TaskMaster") y documentación ("Task Manager"). Confunde al usuario y al mantenedor.
- Principio: Documentación — la doc debe reflejar el comportamiento real.
- Cambio sugerido: unificar el nombre de producto (recomendado "TaskMaster") en `AppLogo`,
  `README.md` y `project.md`.

### 11. [Medio] README con teclas faltantes
- Archivo: `README.md:35-49`
- Problema: la tabla "Teclas" omite `h` (ocultar/mostrar listas vacías, implementado en
  `TaskListWindow.java:152-156`) y la tecla `d` del calendario (quitar fecha,
  `CalendarWindow.java:56`). También dice "La app abre directamente la vista única de
  tareas", pero existen ventanas modales (añadir/editar, calendario, menús).
- Principio: Documentación — exactitud y completitud.
- Cambio sugerido: añadir filas para `h` y para `d` (en contexto de calendario) y ajustar
  la descripción de la vista.

### 12. [Bajo] Constante sin uso `APPLICATION_NAME` en `GoogleAuth`
- Archivo: `src/main/java/tasktracker/google/GoogleAuth.java:24`
- Problema: `APPLICATION_NAME` se declara pero nunca se usa en la clase.
- Principio: Clean Code — eliminar código muerto.
- Cambio sugerido: eliminarla (el `APPLICATION_NAME` real se usa en `GoogleTasksProvider`).

### 13. [Bajo] `App.main` mezcla composición de dependencias y ciclo de vida del terminal
- Archivo: `src/main/java/tasktracker/App.java:19-43`
- Problema: el `main` construye a mano `GoogleAuth` → `GoogleTasksProvider` → `TaskService`
  y además gestiona `Terminal`/`Screen`. `main` declara `throws IOException` y no captura
  `ProviderException`, por lo que un fallo de autenticación/red imprime un stack trace
  crudo en lugar de un mensaje legible.
- Principio: Clean Architecture — composition root separado; Error Handling — no exponer
  stack traces al usuario final.
- Cambio sugerido: extraer un `App.run()` (o `TaskTrackerApp`) que arme el grafo y capture
  `ProviderException`/`IOException` mostrando un mensaje y devolviendo un código de salida.

### 14. [Bajo] `AppLogo.lines()` y `minWidth()` usados solo por tests
- Archivo: `src/main/java/tasktracker/cli/AppLogo.java:15-21`
- Problema: solo `fit()` se usa en producción; `lines()` y `minWidth()` existen únicamente
  para `AppLogoTest`.
- Principio: Clean Code — evitar API pública/paquete solo para tests.
- Cambio sugerido: opcional. Mantenerlos como API de paquete es aceptable, pero considerar
  derivarlos de `fit()` o marcarlos como apoyo de test.

### 15. [Bajo] `project.md` dice "sin frameworks externos" contradiciendo a Lanterna
- Archivo: `openspec/project.md:10`
- Problema: "Java 21 (sin frameworks externos)" pero dos líneas después se documenta
  Lanterna como framework de TUI. Contradicción.
- Principio: Documentación — precisión.
- Cambio sugerido: reformular a "sin framework de inyección de dependencias / Spring" u
  otro matiz que no contradiga el uso de Lanterna.

### 16. [Bajo] Deriva en `REGISTER.md` sobre persistencia del zoom
- Archivo: `openspec/changes/REGISTER.md:36`
- Problema: el resumen del change `020-implement-ui-zoom` dice "con persistencia", pero el
  comportamiento actual y su spec (`ui-zoom/spec.md:77-90`) son de **solo sesión**.
- Principio: Documentación — mantener la fuente de verdad coherente.
- Cambio sugerido: corregir el resumen a "zoom global de solo sesión".

### 17. [Bajo] `pom.xml` sin versión explícita de `maven-compiler-plugin`
- Archivo: `pom.xml:53-92`
- Problema: no se fija la versión del `maven-compiler-plugin` (se hereda una por defecto
  que puede variar entre entornos) ni hay `maven-enforcer-plugin` para garantizar Java 21.
- Principio: Buenas prácticas — builds reproducibles.
- Cambio sugerido: fijar `maven-compiler-plugin` (p. ej. `3.13.0`) con `release=21` y,
  opcionalmente, añadir `maven-enforcer-plugin`.

---

## Plan de ejecución sugerido (agrupado por objetivo)

Orden propuesto, priorizando impacto y bajo riesgo:

1. **Performance al levantar (Alto → primero)**
   - #1 Cachear `Tasks`/transporte en `GoogleTasksProvider`.
   - #2 Cachear flow/transporte y corregir `hasStoredCredentials()` en `GoogleAuth`.
   - #3 (opcional) Cargar tareas por lista en paralelo en `TaskService.load()`.

2. **Mejora de código (Clean Code / Clean Architecture)**
   - #4 Dividir `handleInput` de `TaskListWindow`.
   - #6 Eliminar duplicación de navegación de menús.
   - #7 Extraer helper `call(...)` en `GoogleTasksProvider`.
   - #9 Modelar `due` como `LocalDate` y aislar el formato de Google en el adaptador.
   - #13 Separar composition root y manejo de errores en `App`.

3. **Eliminación de código en desuso**
   - #5 Recortar `TaskProvider` (y sus implementaciones).
   - #12 Quitar `APPLICATION_NAME` sin uso en `GoogleAuth`.
   - #14 (opcional) Revisar helpers solo-test en `AppLogo`.

4. **Mejores prácticas / correcciones**
   - #8 Paginar la Google Tasks API (o documentar el límite).
   - #17 Fijar versión de `maven-compiler-plugin`.

5. **Actualización de documentación**
   - #10 Unificar el nombre de producto ("TaskMaster").
   - #11 Añadir teclas `h` y `d` al README y corregir la descripción de la vista.
   - #15 Ajustar "sin frameworks externos" en `project.md`.
   - #16 Corregir resumen de zoom en `REGISTER.md`.

Cada hallazgo lista el archivo y línea exactos; el modo Plan puede aplicar los cambios uno
a uno respetando la convención de registrar el delta en `openspec/changes/`.
