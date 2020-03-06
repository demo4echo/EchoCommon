#!/usr/bin/env sh

cd ..
CURRENT_FOLDER_NAME=${PWD##*/}
cd -

docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.gradle:/root/.gradle -v $HOME/.helm:/root/.helm -v "$PWD/../..":/root/repo -w /root/repo -e MARK_OFF_CLUSTER_INVOCATION_ENV_VAR='dc' -e COMMON_SUB_MODULE_NAME_ENV_VAR="$CURRENT_FOLDER_NAME" demo4echo/alpine_openjdk8_k8scdk:latest sh
