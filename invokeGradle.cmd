@ECHO OFF

SET MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

REM CD /D "%~dp0"
REM PUSHD "%~dp0"
COPY .gradle\gradle.properties %USERPROFILE%\.gradle\gradle.properties /Y
COPY .gradle\init.gradle %USERPROFILE%\.gradle\init.gradle /Y
REM POPD

PUSHD ..
CALL gradlew.bat %*
POPD

REM DEL %USERPROFILE%\.gradle\gradle.properties
REM DEL %USERPROFILE%\.gradle\init.gradle
