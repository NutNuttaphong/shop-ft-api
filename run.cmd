@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MVN_CMD=%~dp0.mvn\apache-maven-3.9.9\bin\mvn.cmd"

if "%~1"=="" (
    echo Usage: run.cmd [compile^|run^|build^|test]
    echo.
    echo Commands:
    echo   compile  - Compile the project
    echo   run      - Start the Spring Boot server
    echo   build    - Build the project (skip tests)
    echo   test     - Run tests
    exit /b 0
)

if "%~1"=="compile" (
    call "%MVN_CMD%" compile
) else if "%~1"=="run" (
    call "%MVN_CMD%" spring-boot:run
) else if "%~1"=="build" (
    call "%MVN_CMD%" package -DskipTests
) else if "%~1"=="test" (
    call "%MVN_CMD%" test
) else (
    call "%MVN_CMD%" %*
)

endlocal
