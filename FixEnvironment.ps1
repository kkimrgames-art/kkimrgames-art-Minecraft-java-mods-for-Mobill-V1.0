# FixEnvironment.ps1
# Run this script to permanently add the Eaglercraft local-tools JDK to your Windows PATH.
# This fixes the "ObjectNotFound: (jar:String)" error in PowerShell.

$ToolsDir = Join-Path $PSScriptRoot "local-tools"
$JdkDirs = Get-ChildItem -Path $ToolsDir -Directory -Filter "jdk-*" | Select-Object -First 1

if ($null -ne $JdkDirs) {
    $BinPath = Join-Path $JdkDirs.FullName "bin"
    $CurrentPath = [Environment]::GetEnvironmentVariable("PATH", [EnvironmentVariableTarget]::User)
    
    if ($CurrentPath -notmatch [regex]::Escape($BinPath)) {
        if (-not [string]::IsNullOrEmpty($CurrentPath) -and -not $CurrentPath.EndsWith(";")) {
            $CurrentPath += ";"
        }
        $NewPath = $CurrentPath + $BinPath
        [Environment]::SetEnvironmentVariable("PATH", $NewPath, [EnvironmentVariableTarget]::User)
        Write-Host "Success: Added '$BinPath' to your User PATH environment variable." -ForegroundColor Green
        Write-Host "Please restart your PowerShell or Command Prompt for the changes to take effect." -ForegroundColor Yellow
    } else {
        Write-Host "Note: '$BinPath' is already in your PATH." -ForegroundColor Cyan
    }
} else {
    Write-Host "Error: Could not find any jdk-* folder inside local-tools. Have you run the setup?" -ForegroundColor Red
}

Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
