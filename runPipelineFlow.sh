#!/usr/bin/env sh

export MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

cp -ar ./.gradle/gradle.properties $USER/.gradle/gradle.properties

cd ..

./gradlew $# AIO
