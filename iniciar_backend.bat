@echo off
title SIMACMART - Backend (Puerto 8080)
echo =================================================================
echo  INICIANDO BACKEND KOTLIN SIMACMART (Puerto 8080)
echo =================================================================
cd /d "%~dp0SIMACMART-Backend"
call gradlew.bat run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] No se pudo iniciar el backend.
    echo Verifica que tengas Java JDK 17 o 21 instalado y configurado en el PATH.
    echo Consulta GUIA_INSTALACION_LOCAL.md para mas detalles.
    pause
)
