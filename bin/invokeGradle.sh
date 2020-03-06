#!/usr/bin/env sh

export MARK_OFF_CLUSTER_INVOCATION_ENV_VAR="dc"

# Absolute path to this script, e.g. /home/user/bin/foo.sh
#SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
#SCRIPT_PATH=$(dirname "$SCRIPT")

#cd "$SCRIPT_PATH"
cp -ar ../.setup/.gradle $HOME/.gradle
#cd -

cd ../..
./gradlew $*
cd -

#rm $HOME/.gradle/gradle.properties
#rm $HOME/.gradle/init.gradle
