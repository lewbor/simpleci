#!/usr/bin/env bash
set -e
set -o verbose

php --version

composer install --no-interaction --no-progress
bin/console assets:install
pushd frontend
yarn install
node_modules/.bin/webpack -p
popd
