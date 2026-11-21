$ErrorActionPreference = 'SilentlyContinue'
$roots = @("$env:ProgramFiles","${env:ProgramFiles(x86)}","$env:LOCALAPPDATA","$env:APPDATA",'E:\pulse','C:\Program Files\Java')
$exes  = @(); $w = where.exe java 2>$null; if ($w) { $exes += $w }
foreach ($r in $roots) { if (Test-Path $r) { $exes += (Get-ChildItem $r -Filter java.exe -Recurse -Depth 6 | ForEach-Object FullName) } }
$homes = $exes | ForEach-Object { Split-Path (Split-Path $_ -Parent) -Parent } | Sort-Object -Unique
$jdk21 = $homes | Where-Object { (& "$_\bin\java.exe" -version 2>&1 | Out-String) -match '"21' } | Select-Object -First 1
if (-not $jdk21) { Write-Host "JDK 21 НЕ НАЙДЕН. Найденные Java:" -ForegroundColor Red; $homes | ForEach-Object { "$_  ->  " + (& "$_\bin\java.exe" -version 2>&1 | Select-Object -First 1) }; return }
Write-Host "JAVA_HOME = $jdk21" -ForegroundColor Green
$env:JAVA_HOME = $jdk21
$env:PATH = "$jdk21\bin;$env:PATH"
.\gradlew.bat build 2>&1 | Tee-Object -FilePath build-log.txt