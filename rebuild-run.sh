#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

echo "==> Compilando..."
mvn package -q -DskipTests

echo "==> Levantando CLI Task Tracker..."
java -jar target/cli-task-tracker-1.0.0.jar
