@echo off

set MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

call gradlew.bat %* AIO
