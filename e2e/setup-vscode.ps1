<#
.SYNOPSIS
    Copies this project's VS Code configuration from tools\vscode into .vscode.

.DESCRIPTION
    The configs are version-controlled under tools\vscode so they travel with the
    repo. This script installs them into .vscode, which is where VS Code actually
    reads them from.

    Existing files are left alone unless you pass -Force, so your own local
    tweaks are never clobbered.

    What gets installed:
      settings.json    JDK 21 for the language server; Cucumber step navigation
                       (Ctrl+click a Gherkin step to reach its Java method)
      launch.json      Debug configurations, including a long newCommandTimeout
                       so the Appium session survives sitting on a breakpoint
      tasks.json       mvn test variants, dry run, report, Appium server
      extensions.json  Recommended extensions

.PARAMETER Force
    Overwrite files that already exist in .vscode.

.EXAMPLE
    .\setup-vscode.ps1
#>

[CmdletBinding()]
param([switch] $Force)

$ErrorActionPreference = 'Continue'

$root = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Location).Path }
$source = Join-Path $root 'tools\vscode'
$target = Join-Path $root '.vscode'

if (-not (Test-Path $source)) {
    Write-Host "ERROR: $source not found. Run this from the e2e project root." -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Path $target -Force | Out-Null

$installed = 0
$skipped = 0

foreach ($file in @(Get-ChildItem -Path $source -Filter '*.json' -File)) {
    $destination = Join-Path $target $file.Name
    if ((Test-Path $destination) -and -not $Force) {
        Write-Host "  skipped  $($file.Name)  (already exists - pass -Force to overwrite)" -ForegroundColor Yellow
        $skipped++
        continue
    }
    Copy-Item -Path $file.FullName -Destination $destination -Force
    Write-Host "  wrote    .vscode\$($file.Name)" -ForegroundColor Green
    $installed++
}

Write-Host ''
Write-Host "$installed file(s) installed, $skipped skipped." -ForegroundColor Cyan

if ($installed -gt 0) {
    Write-Host ''
    Write-Host 'Now, in VS Code:' -ForegroundColor Cyan
    Write-Host '  1. Open THIS folder (e2e) as the workspace root - not the parent repo,'
    Write-Host '     or the Cucumber globs will not resolve and step navigation stays dead.'
    Write-Host '  2. Install the recommended extensions when prompted.'
    Write-Host '  3. Ctrl+Shift+P -> Developer: Reload Window'
    Write-Host ''
    Write-Host 'Then Ctrl+click any step in a .feature file to jump to its Java method.'
}
