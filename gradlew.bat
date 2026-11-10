@echo off
setlocal EnableExtensions
set "GRADLE_VERSION=9.2.1"
set "GRADLE_HOME=%USERPROFILE%\.gradle\manual\gradle-%GRADLE_VERSION%"
set "GRADLE_EXE=%GRADLE_HOME%\bin\gradle.bat"

if exist "%GRADLE_EXE%" goto run_gradle
where gradle >nul 2>nul
if not errorlevel 1 (
  gradle %*
  exit /b %ERRORLEVEL%
)

echo [DercoVisuals] Gradle %GRADLE_VERSION% is not cached. Downloading it once...
set "ZIP=%TEMP%\gradle-%GRADLE_VERSION%-bin.zip"
set "URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri '%URL%' -OutFile '%ZIP%'"
if errorlevel 1 exit /b 1
if not exist "%USERPROFILE%\.gradle\manual" mkdir "%USERPROFILE%\.gradle\manual"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Expand-Archive -LiteralPath '%ZIP%' -DestinationPath '%USERPROFILE%\.gradle\manual' -Force"
if errorlevel 1 exit /b 1
del "%ZIP%" >nul 2>nul

:run_gradle
call "%GRADLE_EXE%" %*
exit /b %ERRORLEVEL%
