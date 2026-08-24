# Empaquetar jar ejecutable autocontenido

## Why

Al agregar JLine (dependencia runtime) para el modo interactivo, la app dejó de
poder ejecutarse con `java -cp target/classes tasktracker.App`, que no incluye
las dependencias en el classpath (producía `NoClassDefFoundError` al invocar
`list`). Se necesita un artefacto autocontenido para correr la aplicación.

## What Changes

- `pom.xml`: agrega `maven-shade-plugin` (fase `package`) para producir un jar
  ejecutable con `Main-Class: tasktracker.App`, que incluye JLine y fusiona
  `META-INF/services` (usados por JLine vía `ServiceLoader`).
- Filtra archivos de firma (`META-INF/*.SF|DSA|RSA`) para evitar errores de
  digest en dependencias firmadas.

## Impact

- `mvn package` genera `target/cli-task-tracker-1.0.0.jar` autocontenido.
- Comando de ejecución: `java -jar target/cli-task-tracker-1.0.0.jar`.
- `java -cp target/classes ...` deja de ser una forma válida de ejecución.
- Verificación: `mvn package` + smoke test con `java -jar` (comandos y `list`
  interactivo).
