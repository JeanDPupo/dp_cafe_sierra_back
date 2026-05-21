$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$mavenCmd = "C:\Users\delga\AppData\Local\Temp\apache-maven-3.9.9\bin\mvn.cmd"
$jarPath = Join-Path $projectRoot "target\cowork-0.0.1-SNAPSHOT.jar"
$javaCmd = "C:\Program Files\Java\jdk-20\bin\java.exe"
$healthUrl = "http://127.0.0.1:8080/api/v1/health"
$logDir = Join-Path $projectRoot "logs"
$stdoutLog = Join-Path $logDir "backend.out.log"
$stderrLog = Join-Path $logDir "backend.err.log"

if (-not (Test-Path $mavenCmd)) {
    throw "No se encontro Maven en $mavenCmd"
}

if (-not (Test-Path $javaCmd)) {
    throw "No se encontro Java en $javaCmd"
}

$existing = Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -eq "java.exe" -and
        $_.CommandLine -like "*cowork-0.0.1-SNAPSHOT.jar*"
    }

if ($existing) {
    $existing | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
    Start-Sleep -Seconds 2
}

Push-Location $projectRoot
try {
    & $mavenCmd clean package -DskipTests

    if (-not (Test-Path $jarPath)) {
        throw "No se genero el jar esperado en $jarPath"
    }

    New-Item -ItemType Directory -Force -Path $logDir | Out-Null
    Remove-Item $stdoutLog, $stderrLog -ErrorAction SilentlyContinue

    $process = Start-Process `
        -FilePath $javaCmd `
        -ArgumentList "-jar", $jarPath `
        -WorkingDirectory $projectRoot `
        -WindowStyle Hidden `
        -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog `
        -PassThru

    $ready = $false
    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Seconds 2

        try {
            $response = Invoke-WebRequest -UseBasicParsing $healthUrl
            if ($response.StatusCode -eq 200) {
                $ready = $true
                break
            }
        }
        catch {
        }
    }

    if (-not $ready) {
        throw "El backend no quedo listo a tiempo. Revisa $stderrLog y $stdoutLog"
    }

    Write-Host "Backend iniciado con PID $($process.Id)"
    Write-Host "Health: $healthUrl"
    Write-Host "Logs: $stdoutLog"
}
finally {
    Pop-Location
}
