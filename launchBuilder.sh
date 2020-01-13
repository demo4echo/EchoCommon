#!/usr/bin/env sh

docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.gradle:/root/.gradle -v $HOME/.helm:/root/.helm -v `pwd`:/root/repo -w /root/repo -e MARK_OFF_CLUSTER_INVOCATION_ENV_VAR='dc' demo4echo/alpine_openjdk8_k8scdk:latest ash
