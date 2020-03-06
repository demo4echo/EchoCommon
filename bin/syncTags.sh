#!/usr/bin/env sh

cd ../.. && git tag -d $(git tag) && git pull --tags && cd -
