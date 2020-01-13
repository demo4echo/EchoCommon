#!/usr/bin/env sh

export MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

./gradlew $# AIO
