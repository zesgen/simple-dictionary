@echo off
setlocal

rem ----------------------------------------------------------------------------
rem Script parameters
set OUT_DIR=dist
set APP_NAME=simple-dictionary-client
set APP_MAVEN_SUFFIX=1.0-SNAPSHOT-jar-with-dependencies
set MAVEN_TARGET_DIR=target


rem ----------------------------------------------------------------------------
rem Script execution

echo Preparing to make...

set APP_MAVEN_NAME=%APP_NAME%-%APP_MAVEN_SUFFIX%

rem Directory where script has been started
set DIR=%~dp0
set DIR=%DIR:~0,-1%


IF not exist %DIR%\%OUT_DIR% (
  echo [ERR ] Can't find dir [%OUT_DIR%] for output files. Making has been canceled.
  exit /b 1
)
echo [ OK ] Preparation completed successfully. Running Maven wrapper to make...

call mvnw.cmd package
IF %ERRORLEVEL% NEQ 0 (
  echo [ERR ] Maven building finished with error. Making has been canceled.
  exit /b 1
)

echo [ OK ] Maven building finished successfully. Copying file [%DIR%\%MAVEN_TARGET_DIR%\%APP_MAVEN_NAME%.jar] to file [%DIR%\%OUT_DIR%\%APP_NAME%.jar]...
copy /Y %DIR%\%MAVEN_TARGET_DIR%\%APP_MAVEN_NAME%.jar %DIR%\%OUT_DIR%\%APP_NAME%.jar
IF %ERRORLEVEL% NEQ 0 (
  echo [ERR ] Copying finished with error. Making has been canceled.
  exit /b 1
)

IF NOT "%~1" == "-kt" (
  echo [ OK ] Copying finished successfully. Directory [%DIR%\%MAVEN_TARGET_DIR%] is being removed...
  rd /S /Q %DIR%\%MAVEN_TARGET_DIR%
)
echo [ OK ] All operations were completed successfully. The application is ready for use.
exit /B 0

