@echo off
setlocal
cd /d "%~dp0"

if not exist "tools\launch.ps1" (
  echo [DercoVisuals] Missing tools\launch.ps1. Re-download the complete project or use a GitHub Release JAR.
  pause
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\launch.ps1"
set "RESULT=%ERRORLEVEL%"
if not "%RESULT%"=="0" (
  echo.
  echo [DercoVisuals] The update/build/launch process reported an error: %RESULT%.
  pause
)
exit /b %RESULT%
