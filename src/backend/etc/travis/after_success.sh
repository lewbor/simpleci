#!/usr/bin/env bash
set -e

if [ ! -z "$TRAVIS_TAG" ]; then
    docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"

    pushd dispatcher
    docker build -t simpleci/dispatcher:$TRAVIS_TAG .
    docker tag simpleci/dispatcher:$TRAVIS_TAG simpleci/dispatcher:latest
    docker push simpleci/dispatcher:$TRAVIS_TAG
    docker push simpleci/dispatcher:latest
    popd

    pushd worker
    docker build -t simpleci/worker:$TRAVIS_TAG .
    docker tag simpleci/worker:$TRAVIS_TAG simpleci/worker:latest
    docker push simpleci/worker:$TRAVIS_TAG
    docker push simpleci/worker:latest
    popd
else
    docker build -t simpleci/frontend:experimental .
    docker push simpleci/frontend:experimental
fi


