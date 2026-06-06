@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper startup batch script for Windows
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%~dp0.mvn\wrapper\maven-wrapper.properties"

@REM Check for JAVA_HOME
if not "%JAVA_HOME%"=="" goto javaHomeSet
echo Error: JAVA_HOME is not set.
exit /b 1

:javaHomeSet
set JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM Check if wrapper jar exists, if not download it
if exist %WRAPPER_JAR% goto runMaven

@REM Download maven-wrapper.jar
echo Downloading Maven Wrapper...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar', '%~dp0.mvn\wrapper\maven-wrapper.jar')"

:runMaven
%JAVA_EXE% -jar %WRAPPER_JAR% %*
