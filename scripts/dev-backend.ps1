Param(
    [switch]$Watch
)

Push-Location "$PSScriptRoot/..\backend"

if ($Watch) {
    mvn spring-boot:run
} else {
    mvn -q -DskipTests package
    java -jar target/backend-0.0.1-SNAPSHOT.jar
}

Pop-Location


