#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

if [ -f ".env" ]; then
  source .env
fi

echo "==> Compilando (sin tests ni empaquetado)..."
mvn -q -DskipTests compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt

echo "==> Levantando CLI Task Tracker..."
java -cp "target/classes:$(cat target/classpath.txt)" tasktracker.App
