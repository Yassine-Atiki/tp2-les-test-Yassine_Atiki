param(
    [ValidateSet('Test1','Test2Couts','Test3Traducteur','Test4Embeddings','Test5Rag1','Test6Rag2','Test7Outils','TestMeteo')]
    [string]$Test = 'Test7Outils',
    [switch]$Local
)
$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    & mvn.cmd compile dependency:build-classpath '-Dmdep.outputFile=target/classpath.txt'
    if ($LASTEXITCODE -ne 0) { throw 'La compilation Maven a échoué.' }
    $classpath = (Join-Path (Get-Location) 'target/classes') + ';' + (Get-Content -Raw target/classpath.txt).Trim()
    $classe = if ($Test -eq 'TestMeteo') { 'ma.emsi.yassine.atiki.outil.meteo.TestMeteo' } else { 'ma.emsi.yassine.atiki.' + $Test }
    $arguments = @('-Dfile.encoding=UTF-8','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-cp',$classpath,$classe)
    if ($Local) {
        if ($Test -ne 'TestMeteo') { throw '-Local ne concerne que TestMeteo.' }
        $arguments += '--local'
    }
    & java @arguments
    if ($LASTEXITCODE -ne 0) { throw "L'exécution a échoué (code $LASTEXITCODE)." }
} finally {
    Pop-Location
}
