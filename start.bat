@echo off
setlocal
cd /d "%~dp0"
title DercoVisuals - Launch Client

echo ============================================
echo   DercoVisuals - Launching Minecraft Fabric
echo ============================================
echo.

if exist "..\Pulse\launch.ps1" (
    powershell -NoProfile -ExecutionPolicy Bypass -File "..\Pulse\launch.ps1"
    set "RC=%ERRORLEVEL%"
) else (
    echo [ERROR] Launcher not found at ..\Pulse\launch.ps1
    set "RC=1"
)

echo.
if not "%RC%"=="0" (
    echo Launcher exited with code %RC%.
    pause
)
endlocal
