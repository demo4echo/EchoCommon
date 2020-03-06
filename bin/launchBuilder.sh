#!/usr/bin/env sh

CURRENT_FOLDER_NAME=${PWD##*/}

cd ..
COMMON_SUB_MODULE_FOLDER_NAME=${PWD##*/}
cd -

cd ../..
REPO_FOLDER_PATH=$PWD
cd -

docker run \
		-it \
		--rm \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $HOME/.gradle:/root/.gradle \
		-v $HOME/.helm:/root/.helm \
		-v "$REPO_FOLDER_PATH":/root/repo \
		-w /root/repo/$COMMON_SUB_MODULE_FOLDER_NAME/$CURRENT_FOLDER_NAME \
		-e MARK_OFF_CLUSTER_INVOCATION_ENV_VAR='dc' \
		-e COMMON_SUB_MODULE_NAME_ENV_VAR="$COMMON_SUB_MODULE_FOLDER_NAME" \
		demo4echo/alpine_openjdk8_k8scdk:latest \
		ash
