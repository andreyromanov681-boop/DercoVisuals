$ErrorActionPreference = 'Stop'
$RepoUrl = 'https://github.com/andreyromanov681-boop/DercoVisuals.git'
$Branch = 'main'
$SourceRoot = Split-Path -Parent $PSScriptRoot
$ProjectRoot = $SourceRoot

function Write-Step([string]$Message) {
    Write-Host "`n[DercoVisuals] $Message" -ForegroundColor Cyan
}

function Invoke-Git([string[]]$GitArgs, [string]$WorkingDirectory) {
    Push-Location $WorkingDirectory
    try {
        & git @GitArgs
        if ($LASTEXITCODE -ne 0) { throw "Git command failed: git $($GitArgs -join ' ')" }
    } finally {
        Pop-Location
    }
}

function Copy-LightweightSources([string]$From, [string]$To) {
    $items = @(
        'README.md', 'LICENSE', 'AGENTS.md', 'CONTRIBUTORS.md', '.gitignore',
        'build.gradle', 'settings.gradle', 'gradle.properties', '1.bat',
        'gradlew.bat', 'gradlew', 'src', 'tools', '.github', 'docs'
    )
    foreach ($item in $items) {
        $source = Join-Path $From $item
        if (Test-Path $source) {
            $destination = Join-Path $To $item
            $parent = Split-Path -Parent $destination
            if ($parent -and !(Test-Path $parent)) { New-Item -ItemType Directory -Path $parent -Force | Out-Null }
            if ((Get-Item $source).PSIsContainer) {
                if (!(Test-Path $destination)) { New-Item -ItemType Directory -Path $destination -Force | Out-Null }
                & robocopy $source $destination /E /NFL /NDL /NJH /NJS /NP | Out-Null
                if ($LASTEXITCODE -ge 8) { throw "Could not copy source directory: $item" }
            } else {
                Copy-Item -LiteralPath $source -Destination $destination -Force
            }
        }
    }
}

function Push-ProjectSources([string]$Directory) {
    Write-Step 'Publishing lightweight source and documentation files to GitHub'
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
    & git -C $Directory push origin $Branch
    if ($LASTEXITCODE -ne 0) {
        Write-Host '[DercoVisuals] GitHub push was not permitted. Continuing with the local source; players do not need repository write access.' -ForegroundColor Yellow
    }
}

function Get-ReleaseJar {
    Write-Step 'Looking for the latest public release JAR'
    $release = Invoke-RestMethod -Uri 'https://api.github.com/repos/andreyromanov681-boop/DercoVisuals/releases/latest' -Headers @{ 'User-Agent' = 'DercoVisuals-Updater' }
    $asset = $release.assets | Where-Object { $_.name -like 'DercoVisuals-*.jar' -and $_.name -notlike '*-sources.jar' } | Select-Object -First 1
    if (-not $asset) { throw 'No DercoVisuals release JAR is published yet. Run this script from a developer Git checkout after the first successful build.' }
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
    Write-Step 'DercoVisuals updater / builder'
    $java = Get-Command java -ErrorAction SilentlyContinue
    if (-not $java) { throw 'Java 21 is required. Install a Java 21 JDK/JRE and run 1.bat again.' }
    $javaText = (& java -version 2>&1 | Out-String)
    if ($javaText -notmatch 'version "(21|22|23|24|25)') {
        throw "Java 21 or newer is required. Detected: $($javaText.Trim())"
    }

    if (Test-Path (Join-Path $SourceRoot '.git')) {
        $ProjectRoot = $SourceRoot
        Write-Step 'Updating the developer checkout from origin/main'
        Invoke-Git @('pull', '--rebase', 'origin', $Branch) $ProjectRoot
    } elseif (Get-Command git -ErrorAction SilentlyContinue) {
        $ProjectRoot = Join-Path $env:TEMP 'DercoVisuals-source'
        if (!(Test-Path (Join-Path $ProjectRoot '.git'))) {
            if (Test-Path $ProjectRoot) { Remove-Item $ProjectRoot -Recurse -Force }
            Write-Step 'Cloning the source repository into the temporary build folder'
            Invoke-Git @('clone', '--branch', $Branch, $RepoUrl, $ProjectRoot) $env:TEMP
        } else {
            Invoke-Git @('pull', '--rebase', 'origin', $Branch) $ProjectRoot
        }
        Copy-LightweightSources $SourceRoot $ProjectRoot
    } else {
        $releaseJar = Get-ReleaseJar
        Install-Mod $releaseJar
        Open-MinecraftLauncher
        exit 0
    }

    Push-ProjectSources $ProjectRoot
    Write-Step 'Building DercoVisuals with Gradle (first build downloads dependencies)'
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
    Open-MinecraftLauncher
    Write-Host "`nDercoVisuals is installed. Select your Fabric 1.21.11 profile in Minecraft Launcher." -ForegroundColor Green
} catch {
    Write-Host "`n[DercoVisuals ERROR] $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
