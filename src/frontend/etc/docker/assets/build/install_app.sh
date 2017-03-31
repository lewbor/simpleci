#!/usr/bin/env bash

# installing composer deps here to be sure that all php extensions and
# other exists in this image
cd ${APP_DIR}
sudo -HEu ${APP_USER} composer install --no-dev --no-interaction --no-progress
