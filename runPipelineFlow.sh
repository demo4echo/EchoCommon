#!/usr/bin/env sh

export MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPT_PATH=$(dirname "$SCRIPT")

cd "$SCRIPT_PATH"
cp ./.gradle/gradle.properties $HOME/.gradle/gradle.properties
cd -

echo "tiran-$#"

#./gradlew $# AIO

rm $HOME/.gradle/gradle.properties
