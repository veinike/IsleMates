# IsleMates dev loop: watches src/main, runs mvn compile, restarts javafx:run after a short debounce.
# Usage (from repo root):  powershell -NoProfile -ExecutionPolicy Bypass -File scripts\dev-hot-reload.ps1
# Optional: -DebounceMs 1200
param(
    [int]$DebounceMs = 900
)

$ErrorActionPreference = "Stop"
$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
if (-not $env:JAVA_HOME) {
    $adoptium = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
    if (Test-Path $adoptium) {
        $env:JAVA_HOME = $adoptium
    }
}
$Maven = "mvn"
$SignalFile = Join-Path $env:TEMP "palsandpalms-dev-reload.flag"
$script:CmdPid = $null

function Write-DevLog {
    param([string]$Message, [string]$Color = "Gray")
    Write-Host ("[{0}] {1}" -f (Get-Date -Format "HH:mm:ss"), $Message) -ForegroundColor $Color
}

function Stop-DevGame {
    if ($null -ne $script:CmdPid) {
        & taskkill.exe /PID $script:CmdPid /T /F 2>$null | Out-Null
        $script:CmdPid = $null
    }
    Start-Sleep -Milliseconds 350
}

function Start-DevGame {
    Stop-DevGame
    $cmdLine = "/c cd /d `"$ProjectRoot`" && $Maven -q javafx:run"
    $p = Start-Process -FilePath "cmd.exe" -ArgumentList $cmdLine -PassThru -WindowStyle Minimized
    $script:CmdPid = $p.Id
    Write-DevLog "Game process started (cmd pid $($script:CmdPid))." "Green"
}

function Invoke-Compile {
    Push-Location $ProjectRoot
    try {
        & $Maven @("-q", "compile", "-DskipTests")
        return ($LASTEXITCODE -eq 0)
    } finally {
        Pop-Location
    }
}

Remove-Item $SignalFile -Force -ErrorAction SilentlyContinue

$watchPath = Join-Path $ProjectRoot "src\main"
if (-not (Test-Path $watchPath)) {
    Write-DevLog "Missing $watchPath" "Red"
    exit 1
}

$fsw = New-Object System.IO.FileSystemWatcher
$fsw.Path = $watchPath
$fsw.Filter = "*.*"
$fsw.IncludeSubdirectories = $true
$fsw.NotifyFilter = [System.IO.NotifyFilters]::LastWrite -bor [System.IO.NotifyFilters]::FileName -bor [System.IO.NotifyFilters]::Size
$fsw.EnableRaisingEvents = $true

$msg = @{ Flag = $SignalFile; Root = $ProjectRoot }
$onFsEvent = {
    $path = $Event.SourceEventArgs.FullPath
    if ($path -match '[\\/]target[\\/]' -or $path -match '[\\/]\.git[\\/]') {
        return
    }
    try {
        [IO.File]::WriteAllText($Event.MessageData.Flag, "1")
    } catch {
        # ignore locked / transient editor files
    }
}

Register-ObjectEvent -InputObject $fsw -EventName Changed -SourceIdentifier "PalsDev-Changed" -MessageData $msg -Action $onFsEvent | Out-Null
Register-ObjectEvent -InputObject $fsw -EventName Created -SourceIdentifier "PalsDev-Created" -MessageData $msg -Action $onFsEvent | Out-Null
Register-ObjectEvent -InputObject $fsw -EventName Renamed -SourceIdentifier "PalsDev-Renamed" -MessageData $msg -Action $onFsEvent | Out-Null

Write-Host ""
Write-DevLog "IsleMates hot-reload: editing src/main/** will recompile and restart the game." "Cyan"
Write-DevLog "Debounce: ${DebounceMs}ms. Close this window or press Ctrl+C to stop (game closes too)." "Cyan"
Write-Host ""

if (-not (Invoke-Compile)) {
    Write-DevLog "Initial compile failed; fix errors and save a file to retry." "Red"
    exit 1
}
Start-DevGame

try {
    while ($true) {
        Start-Sleep -Milliseconds 300
        if (-not (Test-Path $SignalFile)) {
            continue
        }
        Remove-Item $SignalFile -Force -ErrorAction SilentlyContinue
        Start-Sleep -Milliseconds $DebounceMs

        Write-DevLog "Change detected; compiling..." "Yellow"
        Stop-DevGame
        if (Invoke-Compile) {
            Write-DevLog "Restarting game." "Green"
            Start-DevGame
        } else {
            Write-DevLog "Compile failed - fix errors, then save again to retry." "Red"
        }
    }
} finally {
    Stop-DevGame
    $fsw.EnableRaisingEvents = $false
    $fsw.Dispose()
    foreach ($id in @("PalsDev-Changed", "PalsDev-Created", "PalsDev-Renamed")) {
        Unregister-Event -SourceIdentifier $id -ErrorAction SilentlyContinue
    }
    Remove-Item $SignalFile -Force -ErrorAction SilentlyContinue
    Write-DevLog "Stopped." "Gray"
}
