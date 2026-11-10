$ErrorActionPreference = 'Stop'
$RepoUrl = 'https://github.com/andreyromanov681-boop/DercoVisuals.git'
$Branch = 'main'
$ProjectRoot = Split-Path -Parent $PSScriptRoot

function Write-Step([string]$Message) {
    Write-Host "`n[DercoVisuals] $Message" -ForegroundColor Cyan
}

function Invoke-Git([string[]]$GitArgs, [string]$WorkingDirectory) {
    & git -C $WorkingDirectory @GitArgs
    if ($LASTEXITCODE -ne 0) { throw "Git command failed: git $($GitArgs -join ' ')" }
}

function Push-ProjectSources([string]$Directory) {
    Write-Step 'Staging only lightweight DercoVisuals sources and documentation'
    $files = @(
        'README.md', 'LICENSE', 'AGENTS.md', 'CONTRIBUTORS.md', '.gitignore',
        'build.gradle', 'settings.gradle', 'gradle.properties', '1.bat',
        'gradlew.bat', 'gradlew', 'src', 'tools', '.github', 'docs'
    )
    foreach ($file in $files) {
        & git -C $Directory add -- $file
        if ($LASTEXITCODE -ne 0) { throw "Git could not stage $file" }
    }

    & git -C $Directory diff --cached --quiet
    if ($LASTEXITCODE -ne 0) {
        & git -C $Directory -c user.name='enizer6569' -c user.email='224441679+andreyromanov681-boop@users.noreply.github.com' commit -m 'Update DercoVisuals sources'
        if ($LASTEXITCODE -ne 0) { throw 'Could not create the source commit.' }
    }

    & git -C $Directory diff --quiet
    if ($LASTEXITCODE -ne 0) {
        throw 'There are other unstaged tracked changes. Commit or stash them before running 1.bat; nothing was discarded.'
    }

    Write-Step 'Rebasing the local source commit onto origin/main'
    Invoke-Git @('pull', '--rebase', 'origin', $Branch) $Directory
    Write-Step 'Pushing source changes to GitHub'
    Invoke-Git @('push', 'origin', $Branch) $Directory
}

function Get-ReleaseJar {
    Write-Step 'Downloading the latest small player build from GitHub Releases'
    $release = Invoke-RestMethod -Uri 'https://api.github.com/repos/andreyromanov681-boop/DercoVisuals/releases/latest' -Headers @{ 'User-Agent' = 'DercoVisuals-Updater' }
    $asset = $release.assets | Where-Object { $_.name -like 'DercoVisuals-*.jar' -and $_.name -notlike '*-sources.jar' -and $_.name -notlike '*-dev.jar' } | Select-Object -First 1
    if (-not $asset) { throw 'No DercoVisuals player JAR is published yet. Check the GitHub Actions build.' }
    $target = Join-Path $env:TEMP $asset.name
    Invoke-WebRequest -Uri $asset.browser_download_url -OutFile $target -Headers @{ 'User-Agent' = 'DercoVisuals-Updater' }
    return $target
}

function Install-Mod([string]$JarPath) {
    $mods = Join-Path $env:APPDATA '.minecraft\mods'
    if (!(Test-Path $mods)) { New-Item -ItemType Directory -Path $mods -Force | Out-Null }
    Get-ChildItem -Path $mods -Filter 'DercoVisuals-*.jar' -File -ErrorAction SilentlyContinue |
        Remove-Item -Force
    Copy-Item -LiteralPath $JarPath -Destination (Join-Path $mods (Split-Path $JarPath -Leaf)) -Force
    Write-Step "Installed $(Split-Path $JarPath -Leaf) into $mods"
}

function Open-MinecraftLauncher {
    Write-Step 'Opening Minecraft Launcher'
    $launcher = Get-Command 'MinecraftLauncher.exe' -ErrorAction SilentlyContinue
    if ($launcher) {
        Start-Process $launcher.Source
        return
    }
    $paths = @(
        "$env:ProgramFiles\Minecraft Launcher\MinecraftLauncher.exe",
        "${env:ProgramFiles(x86)}\Minecraft Launcher\MinecraftLauncher.exe",
        "$env:LOCALAPPDATA\Programs\Minecraft Launcher\MinecraftLauncher.exe"
    )
    foreach ($path in $paths) {
        if (Test-Path $path) { Start-Process $path; return }
    }
    $app = Get-StartApps | Where-Object { $_.Name -match 'Minecraft Launcher' } | Select-Object -First 1
    if ($app) { Start-Process "shell:AppsFolder\$($app.AppID)"; return }
    try { Start-Process 'minecraft://' } catch { throw 'Could not locate Minecraft Launcher. The mod is installed; open the launcher manually.' }
}

try {
    Write-Step 'DercoVisuals updater'
    if (Test-Path (Join-Path $ProjectRoot '.git')) {
        # Maintainer mode. Only source paths above are staged; pulse-src.zip and caches are not.
        $java = Get-Command java -ErrorAction SilentlyContinue
        if (-not $java) { throw 'Java 21 is required for developer builds. Install a Java 21 JDK and run 1.bat again.' }
        $javaText = (& java -version 2>&1 | Out-String)
        if ($javaText -notmatch 'version "(21|22|23|24|25)') {
            throw "Java 21 or newer is required. Detected: $($javaText.Trim())"
        }

        Push-ProjectSources $ProjectRoot
        Write-Step 'Building DercoVisuals'
        $gradlew = Join-Path $ProjectRoot 'gradlew.bat'
        if (!(Test-Path $gradlew)) { throw 'gradlew.bat is missing from the project.' }
        & $gradlew build --no-daemon
        if ($LASTEXITCODE -ne 0) { throw 'Gradle build failed. Check the error output above; Minecraft was not launched.' }

        $buildDir = Join-Path $ProjectRoot 'build\libs'
        $jar = Get-ChildItem -Path $buildDir -Filter 'DercoVisuals-*.jar' -File |
            Where-Object { $_.Name -notlike '*-sources.jar' -and $_.Name -notlike '*-dev.jar' } |
            Sort-Object LastWriteTime -Descending | Select-Object -First 1
        if (-not $jar) { throw 'Gradle completed but no distributable DercoVisuals JAR was found.' }
        Install-Mod $jar.FullName
    } else {
        # Player mode: no Git clone, no Gradle, no Pulse source archive, no GitHub write access.
        $releaseJar = Get-ReleaseJar
        Install-Mod $releaseJar
    }

    Open-MinecraftLauncher
    Write-Host "`nDercoVisuals is installed. Select your Fabric 1.21.11 profile in Minecraft Launcher." -ForegroundColor Green
} catch {
    Write-Host "`n[DercoVisuals ERROR] $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
