#!/usr/bin/env sh

export MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

#cp ./.gradle/gradle.properties $USER/.gradle/gradle.properties

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPT_PATH=$(dirname "$SCRIPT")
echo $SCRIPT_PATH

#pushd ..
#./gradlew $# AIO
#popd

rm $USER/.gradle/gradle.properties
