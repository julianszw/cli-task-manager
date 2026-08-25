#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

JAR="target/cli-task-tracker-1.0.0.jar"
if [ ! -f "$JAR" ]; then
  echo "==> Compilando..."
  mvn package -q -DskipTests
fi

echo "==> Levantando CLI Task Tracker..."
java -jar "$JAR"
