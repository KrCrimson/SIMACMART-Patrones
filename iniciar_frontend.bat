@echo off
title SIMACMART - Frontend (Puerto 3000)
echo =================================================================
echo  INICIANDO FRONTEND SPA SIMACMART (Puerto 3000)
echo =================================================================
cd /d "%~dp0SIMACMART-Frontend"

where python >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Iniciando servidor web local con Python...
    echo Abre tu navegador en: http://localhost:3000
    start http://localhost:3000
    python -m http.server 3000
    goto end
)

where npx >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Iniciando servidor web local con Node npx serve...
    echo Abre tu navegador en: http://localhost:3000
    start http://localhost:3000
    npx serve -l 3000
    goto end
)

echo.
echo [ERROR] No se encontro ni Python ni Node.js instalados en este equipo.
echo Por favor instala Python 3 (marcando 'Add python.exe to PATH')
echo o consulta la guia paso a paso en GUIA_INSTALACION_LOCAL.md
pause

:end
