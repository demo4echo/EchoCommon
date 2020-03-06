@ECHO OFF

SET MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

REM CD /D "%~dp0"
REM PUSHD "%~dp0"
XCOPY ..\.setup\.gradle %USERPROFILE%\.gradle /I /Y /S /Q
REM POPD

PUSHD ..\..
CALL gradlew.bat %*
POPD

REM DEL %USERPROFILE%\.gradle\gradle.properties
REM DEL %USERPROFILE%\.gradle\init.gradle
