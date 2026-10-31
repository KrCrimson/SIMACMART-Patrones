@echo off
title SIMACMART Backend Launcher
echo =================================================================
echo  INICIANDO MONOLITO MODULAR SIMACMART (UPT)
echo =================================================================

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [AVISO] JDK 21 no encontrado en ruta predeterminada. Usando Java de PATH.
) else (
    set PATH=%JAVA_HOME%\bin;%PATH%
)

echo Compilando y ejecutando pruebas unitarias...
call gradlew.bat test
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Las pruebas unitarias fallaron. Abortando arranque.
    pause
    exit /b %ERRORLEVEL%
)

echo Arrancando servidor HTTP nativo JVM en http://localhost:8080 ...
call gradlew.bat run --quiet
pause
