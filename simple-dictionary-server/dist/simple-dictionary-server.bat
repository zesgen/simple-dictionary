@echo off
rem ---------------------------------------
rem Directory where script has been started
set DIR=%~dp0
set DIR=%DIR:~0,-1%

rem ---------------------------------------
rem Start the jar with arguments
java -jar %DIR%/simple-dictionary-server.jar %*
@echo on