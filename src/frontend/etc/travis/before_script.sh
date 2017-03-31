#!/usr/bin/env bash

composer install --no-interaction --no-progress
bin/console assets:install
pushd frontend
yarn install
node_modules/.bin/webpack -p
popd
