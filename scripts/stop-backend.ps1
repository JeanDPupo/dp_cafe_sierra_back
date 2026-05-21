$processes = Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -eq "java.exe" -and
        $_.CommandLine -like "*cowork-0.0.1-SNAPSHOT.jar*"
    }

if (-not $processes) {
    Write-Host "No hay instancias del backend corriendo."
    exit 0
}

$processes | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
Write-Host "Backend detenido."
