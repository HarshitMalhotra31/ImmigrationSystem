@echo off
setlocal
cd /d "%~dp0"
echo Initializing MongoDB with Default Admin and Officer Accounts...
mvn compile exec:java -Dexec.mainClass="com.immigration.util.DatabaseSetup"
pause
