@echo off
setlocal
cd /d "%~dp0"
echo Starting AI Immigration System from %cd%...
mvn compile exec:java -Dexec.mainClass="com.immigration.MainApplication"
pause
