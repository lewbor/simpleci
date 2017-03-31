#!/usr/bin/env bash

if [ ! -z "$TRAVIS_TAG" ]; then
    docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"

    docker build -t simpleci/frontend:$TRAVIS_TAG .
    docker tag simpleci/frontend:$TRAVIS_TAG simpleci/frontend:latest

    docker push simpleci/frontend:$TRAVIS_TAG
    docker push simpleci/frontend:latest
else
    docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"

    docker build -t simpleci/frontend:experimental .
    docker push simpleci/frontend:experimental
fi
