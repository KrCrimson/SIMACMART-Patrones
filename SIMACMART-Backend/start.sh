#!/usr/bin/env bash
set -e

echo "================================================================="
echo " INICIANDO MONOLITO MODULAR SIMACMART (UPT)"
echo "================================================================="

if [ -n "$JAVA_HOME" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "Ejecutando pruebas unitarias JUnit 5..."
./gradlew test

echo "Iniciando servidor HTTP en puerto ${PORT:-8080}..."
./gradlew run --quiet
