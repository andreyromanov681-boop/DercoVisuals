@echo off
setlocal
cd /d "%~dp0"
title DercoVisuals - Launch Client

echo ============================================
echo   DercoVisuals - Launching Minecraft Fabric
echo ============================================
echo.

if exist "..\DercoVisuals\launch.ps1" (
    powershell -NoProfile -ExecutionPolicy Bypass -File "..\DercoVisuals\launch.ps1"
    set "RC=%ERRORLEVEL%"
) else (
    echo [ERROR] Launcher not found at ..\DercoVisuals\launch.ps1
    set "RC=1"
)

echo.
if not "%RC%"=="0" (
    echo Launcher exited with code %RC%.
    pause
)
endlocal
