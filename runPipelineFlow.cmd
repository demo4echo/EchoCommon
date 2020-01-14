@ECHO OFF

SET MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

REM CD /D "%~dp0"
PUSHD "%~dp0"
COPY .gradle\gradle.properties %USERPROFILE%\.gradle\gradle.properties /Y
POPD

CALL gradlew.bat %* AIO

DEL %USERPROFILE%\.gradle\gradle.properties
