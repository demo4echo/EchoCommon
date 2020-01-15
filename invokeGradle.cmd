@ECHO OFF

SET MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

REM CD /D "%~dp0"
REM PUSHD "%~dp0"
COPY .gradle\gradle.properties %USERPROFILE%\.gradle\gradle.properties /Y
REM POPD

PUSHD ..
CALL gradlew.bat %*
POPD

DEL %USERPROFILE%\.gradle\gradle.properties
