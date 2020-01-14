@ECHO OFF

SET MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

REM CD /D "%~dp0"
PUSHD "%~dp0"

COPY .gradle\gradle.properties %userprofile%\.gradle\gradle.properties /Y

POPD

REM call gradlew.bat %* AIO

DEL %userprofile%\.gradle\gradle.properties
