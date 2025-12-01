@echo off
REM Always run from project root
cd /d %~dp0

REM Run the packaged JAR
java -jar target\LocatorValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar

REM Keep console window open after run
pause