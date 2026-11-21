@echo off
setlocal EnableExtensions
title DercoVisuals - Sync helper and build on GitHub

rem Resolve helper folder WITHOUT a trailing backslash.
for %%I in ("%~dp0.") do set "SOURCE=%%~fI"
for %%I in ("%SOURCE%\..") do set "WORKSPACE=%%~fI"
set "REPO=%WORKSPACE%\DercoVisuals"
set "BRANCH=pulse-foundation"
set "REMOTE=https://github.com/andreyromanov681-boop/DercoVisuals.git"

echo.
echo ==============================================
echo       DERCO VISUALS - GITHUB BUILD
echo ==============================================
echo Source: "%SOURCE%"
echo Git clone: "%REPO%"
echo Branch: %BRANCH%
echo.

where git >nul 2>&1
if errorlevel 1 (
  echo ERROR: Git is not installed or not in PATH.
  goto :fail
)

if not exist "%SOURCE%\build.gradle" if not exist "%SOURCE%\build.gradle.kts" (
  echo WARNING: No Gradle build file found in helper.
  echo This folder may contain tools only, not the complete mod source.
  echo.
  choice /C YN /M "Continue copying helper contents anyway"
  if errorlevel 2 goto :cancel
)

if not exist "%REPO%\.git" (
  echo Cloning repository...
  git clone --branch "%BRANCH%" "%REMOTE%" "%REPO%"
  if errorlevel 1 goto :fail
)

cd /d "%REPO%" || goto :fail

for /f "delims=" %%B in ('git branch --show-current') do set "CURRENT_BRANCH=%%B"
if /I not "%CURRENT_BRANCH%"=="%BRANCH%" (
  git switch "%BRANCH%"
  if errorlevel 1 goto :fail
)

echo.
echo [1/5] Fetching latest GitHub changes...
git fetch origin
if errorlevel 1 goto :fail
git pull --rebase origin "%BRANCH%"
if errorlevel 1 goto :fail

echo.
echo [2/5] Copying files from helper.
echo No destination files are deleted.
robocopy "%SOURCE%" "%REPO%" /E /COPY:DAT /DCOPY:DAT /R:2 /W:2 /XD ".git" ".gradle" "build" "out" ".idea" ".vscode" "node_modules" /XF "pulse-src.zip" "*.log" "*.tmp" "BUILD_DERCO.bat"
set "ROBO=%ERRORLEVEL%"
if %ROBO% GEQ 8 (
  echo ERROR: Robocopy failed with exit code %ROBO%.
  goto :fail
)

echo.
echo [3/5] Checking Gradle project...
if not exist "%REPO%\build.gradle" if not exist "%REPO%\build.gradle.kts" (
  echo ERROR: No build.gradle or build.gradle.kts in repository.
  echo Cannot expect GitHub Actions to build a mod JAR without the Gradle project.
  goto :fail
)

echo.
echo Files changed:
git status --short
echo.
choice /C YN /M "Commit and push these changes"
if errorlevel 2 goto :cancel

echo.
echo [4/5] Checking Git author identity...
set "GITNAME="
set "GITEMAIL="
for /f "delims=" %%N in ('git config user.name 2^>nul') do set "GITNAME=%%N"
if not defined GITNAME (
  set /p GITNAME="Enter Git author name: "
  if not defined GITNAME goto :fail
  git config user.name "%GITNAME%"
)
for /f "delims=" %%E in ('git config user.email 2^>nul') do set "GITEMAIL=%%E"
if not defined GITEMAIL (
  set /p GITEMAIL="Enter Git author email (GitHub email or noreply email): "
  if not defined GITEMAIL goto :fail
  git config user.email "%GITEMAIL%"
)

echo.
echo [5/5] Commit and push...
git add -A
git diff --cached --quiet
if errorlevel 1 (
  git commit -m "Sync helper project and trigger GitHub build"
  if errorlevel 1 goto :fail
) else (
  echo No new file changes to commit.
)

git push origin "%BRANCH%"
if errorlevel 1 goto :fail

echo.
echo PUSH COMPLETED.
echo Opening GitHub Actions. Check the newest run and its Artifacts.
start "" "https://github.com/andreyromanov681-boop/DercoVisuals/actions"
pause
exit /b 0

:cancel
echo Cancelled. No push was made.
pause
exit /b 0

:fail
echo.
echo ERROR: Operation stopped. No further actions were taken.
pause
exit /b 1
